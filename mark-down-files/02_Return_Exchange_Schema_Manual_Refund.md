# Thiết kế schema cho `Return_Exchange` và `Return_Exchange_Item` (phiên bản tối ưu cho hoàn tiền thủ công)

## 1. Mục đích tài liệu

Tài liệu này thiết kế lại schema database cho 2 flow:

1. CUSTOMER hủy đơn trước khi SALES STAFF xác nhận và cần refund cấp **ORDER**.
2. CUSTOMER gửi yêu cầu trả hàng / hoàn tiền do lỗi sản phẩm ở mức **ITEM**.

Phiên bản này tối ưu cho giai đoạn hiện tại:

- STAFF hoàn tiền **thủ công**
- hoàn dựa vào `refund_method` và `refund_account_number`
- staff upload ảnh xác nhận chuyển khoản để lưu evidence

---

## 2. Ghi chú chỉnh sửa so với file gốc

### 2.1. Phần giữ lại

- Giữ mô hình **Header + Item**:
  - `Return_Exchange` = header cấp yêu cầu
  - `Return_Exchange_Item` = dòng chi tiết item
- Giữ ý tưởng:
  - `REFUND` dùng cho scope `ORDER`
  - `RETURN`, `WARRANTY` dùng cho scope `ITEM`
- Giữ bộ trạng thái đơn giản:
  - `PENDING`
  - `APPROVED`
  - `REJECTED`
  - `COMPLETED`

### 2.2. Phần chỉnh sửa / bổ sung

> [CHỈNH SỬA] Bỏ cột `Quantity` ở header vì dễ trùng và lệch với `Return_Exchange_Item`.  
> [CHỈNH SỬA] Đổi tên các cột evidence cho rõ vai trò:
> - `Customer_Evidence_URL`: bằng chứng do CUSTOMER upload
> - `Staff_Refund_Evidence_URL`: ảnh chuyển khoản do STAFF upload  
> [BỔ SUNG MỚI] Thêm `Refund_Account_Name` để staff chuyển khoản chính xác hơn.  
> [BỔ SUNG MỚI] Thêm `Refund_Reference_Code` để lưu mã giao dịch/ghi chú tham chiếu.  
> [BỔ SUNG MỚI] Thêm `Processed_By`, `Processed_Date` để audit bước hoàn tiền thủ công.  
> [BỔ SUNG MỚI] Bổ sung check constraint chặt hơn theo lifecycle:
> - `REJECTED` phải có `Reject_Reason`
> - `APPROVED/COMPLETED` phải có người duyệt
> - `COMPLETED` phải có evidence staff nếu là nghiệp vụ có hoàn tiền  
> [BỔ SUNG MỚI] Bổ sung index/filter index và rule service layer cần có.

---

## 3. Mô hình dữ liệu tổng quan

## 3.1. `Return_Exchange` là header cấp yêu cầu

Một record trong `Return_Exchange` đại diện cho **một hồ sơ yêu cầu xử lý sau bán**.

Ví dụ:

- Hủy đơn trước xác nhận và cần refund toàn order
- Trả 1 item bị lỗi và hoàn tiền
- Gửi yêu cầu bảo hành

## 3.2. `Return_Exchange_Item` là item-level detail

Bảng này chỉ dùng khi request ở mức item (`Request_Scope = ITEM`).

Ví dụ:

- Đơn có 3 `Order_Detail`
- CUSTOMER chỉ muốn trả 1 frame bị lỗi
- Khi đó `Return_Exchange` là header, còn `Return_Exchange_Item` chứa `Order_Detail_ID` và `Quantity` tương ứng

---

## 4. Nguyên tắc thiết kế được chốt

### 4.1. Scope theo loại yêu cầu

- `Return_Type = REFUND`
  - `Request_Scope = ORDER`
  - Không cần `Return_Exchange_Item`

- `Return_Type IN (RETURN, WARRANTY)`
  - `Request_Scope = ITEM`
  - Có thể có một hoặc nhiều dòng `Return_Exchange_Item`

### 4.2. Phạm vi của refund thủ công hiện tại

Refund thủ công sẽ lưu các trường sau ở `Return_Exchange`:

- `Refund_Amount`
- `Refund_Method`
- `Refund_Account_Number`
- `Refund_Account_Name`
- `Refund_Reference_Code`
- `Staff_Refund_Evidence_URL`

### 4.3. Evidence được tách rõ người upload

- Evidence của CUSTOMER: mô tả hàng lỗi / ảnh lỗi / ảnh sản phẩm
- Evidence của STAFF: ảnh chuyển khoản / ảnh xác nhận hoàn tiền

> [CHỈNH SỬA] File gốc dùng `Image_URL` và `Evidence_URL` nhưng chưa thật rõ ai upload. Bản này tách tên rõ hơn để code frontend/backend dễ hiểu.

---

## 5. Script SQL đề xuất

```sql
/* =========================================================
   1) HEADER TABLE: Return_Exchange
   ========================================================= */
CREATE TABLE Return_Exchange (
    Return_Exchange_ID BIGINT IDENTITY(1,1) PRIMARY KEY,
    Order_ID BIGINT NOT NULL,
    User_ID BIGINT NOT NULL,
    Return_Code NVARCHAR(50) NOT NULL UNIQUE,
    Request_Date DATETIME NOT NULL DEFAULT GETDATE(),

    /* [BỔ SUNG MỚI] Ghi chú tổng quát cho request */
    Request_Note NVARCHAR(1000) NULL,

    /* Lý do khách tạo request */
    Return_Reason NVARCHAR(500) NULL,

    /* [CHỈNH SỬA] Evidence do CUSTOMER upload */
    Customer_Evidence_URL NVARCHAR(500) NULL,

    /* REFUND / RETURN / WARRANTY */
    Return_Type NVARCHAR(20) NOT NULL,

    /* ORDER / ITEM */
    Request_Scope NVARCHAR(10) NOT NULL DEFAULT N'ITEM',

    /* Thông tin refund thủ công */
    Refund_Amount DECIMAL(15,2) NULL,
    Refund_Method NVARCHAR(30) NULL,
    Refund_Account_Number NVARCHAR(100) NULL,

    /* [BỔ SUNG MỚI] Giúp staff hoàn đúng người */
    Refund_Account_Name NVARCHAR(100) NULL,

    /* [BỔ SUNG MỚI] Mã tham chiếu giao dịch hoàn tiền nếu có */
    Refund_Reference_Code NVARCHAR(100) NULL,

    /* [CHỈNH SỬA] Evidence do STAFF upload sau khi chuyển khoản */
    Staff_Refund_Evidence_URL NVARCHAR(500) NULL,

    /* PENDING / APPROVED / REJECTED / COMPLETED */
    Status NVARCHAR(30) NOT NULL,

    /* Người duyệt request */
    Approved_By BIGINT NULL,
    Approved_Date DATETIME NULL,

    /* [BỔ SUNG MỚI] Người thực hiện hoàn tiền thủ công */
    Processed_By BIGINT NULL,
    Processed_Date DATETIME NULL,

    /* Lý do từ chối nếu REJECTED */
    Reject_Reason NVARCHAR(500) NULL,

    CONSTRAINT FK_ReturnExchange_Order
        FOREIGN KEY (Order_ID) REFERENCES [Order](Order_ID),

    CONSTRAINT FK_ReturnExchange_User
        FOREIGN KEY (User_ID) REFERENCES [User](User_ID),

    CONSTRAINT FK_ReturnExchange_ApprovedBy
        FOREIGN KEY (Approved_By) REFERENCES [User](User_ID),

    CONSTRAINT FK_ReturnExchange_ProcessedBy
        FOREIGN KEY (Processed_By) REFERENCES [User](User_ID),

    CONSTRAINT CK_ReturnExchange_Status
        CHECK (Status IN (
            N'PENDING', N'APPROVED', N'REJECTED', N'COMPLETED'
        )),

    CONSTRAINT CK_ReturnExchange_ReturnType
        CHECK (Return_Type IN (
            N'REFUND', N'RETURN', N'WARRANTY'
        )),

    CONSTRAINT CK_ReturnExchange_RequestScope
        CHECK (Request_Scope IN (N'ORDER', N'ITEM')),

    /* REFUND = ORDER, RETURN/WARRANTY = ITEM */
    CONSTRAINT CK_ReturnExchange_ScopeByType
        CHECK (
            (Return_Type = N'REFUND' AND Request_Scope = N'ORDER')
            OR
            (Return_Type IN (N'RETURN', N'WARRANTY') AND Request_Scope = N'ITEM')
        ),

    CONSTRAINT CK_ReturnExchange_RefundAmount
        CHECK (Refund_Amount IS NULL OR Refund_Amount >= 0),

    CONSTRAINT CK_ReturnExchange_RefundMethod
        CHECK (
            Refund_Method IS NULL OR Refund_Method IN (
                N'BANK_TRANSFER', N'EWALLET', N'OTHER_MANUAL'
            )
        ),

    /* Nếu REJECTED thì phải có lý do từ chối */
    CONSTRAINT CK_ReturnExchange_RejectReason_Required
        CHECK (
            Status <> N'REJECTED'
            OR (Reject_Reason IS NOT NULL AND LTRIM(RTRIM(Reject_Reason)) <> N'')
        ),

    /* Nếu APPROVED hoặc COMPLETED thì phải có người duyệt */
    CONSTRAINT CK_ReturnExchange_Approval_Required
        CHECK (
            Status NOT IN (N'APPROVED', N'COMPLETED')
            OR (Approved_By IS NOT NULL AND Approved_Date IS NOT NULL)
        ),

    /* Nếu COMPLETED và là nghiệp vụ có hoàn tiền thì phải có dữ liệu hoàn tiền + evidence */
    CONSTRAINT CK_ReturnExchange_CompletedRefund_Required
        CHECK (
            Status <> N'COMPLETED'
            OR Return_Type = N'WARRANTY'
            OR (
                Refund_Amount IS NOT NULL
                AND Refund_Amount > 0
                AND Refund_Method IS NOT NULL
                AND LTRIM(RTRIM(Refund_Method)) <> N''
                AND Refund_Account_Number IS NOT NULL
                AND LTRIM(RTRIM(Refund_Account_Number)) <> N''
                AND Staff_Refund_Evidence_URL IS NOT NULL
                AND LTRIM(RTRIM(Staff_Refund_Evidence_URL)) <> N''
                AND Processed_By IS NOT NULL
                AND Processed_Date IS NOT NULL
            )
        )
);
GO

/* =========================================================
   2) DETAIL TABLE: Return_Exchange_Item
   ========================================================= */
CREATE TABLE Return_Exchange_Item (
    Return_Exchange_Item_ID BIGINT IDENTITY(1,1) PRIMARY KEY,
    Return_Exchange_ID BIGINT NOT NULL,
    Order_Detail_ID BIGINT NOT NULL,
    Quantity INT NOT NULL,

    /* [BỔ SUNG MỚI] Lý do riêng cho item nếu cần */
    Item_Reason NVARCHAR(500) NULL,

    Note NVARCHAR(500) NULL,

    CONSTRAINT FK_ReturnExchangeItem_ReturnExchange
        FOREIGN KEY (Return_Exchange_ID)
        REFERENCES Return_Exchange(Return_Exchange_ID),

    CONSTRAINT FK_ReturnExchangeItem_OrderDetail
        FOREIGN KEY (Order_Detail_ID)
        REFERENCES Order_Detail(Order_Detail_ID),

    CONSTRAINT CK_ReturnExchangeItem_Quantity
        CHECK (Quantity > 0),

    CONSTRAINT UQ_ReturnExchangeItem_ReturnExchange_OrderDetail
        UNIQUE (Return_Exchange_ID, Order_Detail_ID)
);
GO

/* =========================================================
   3) INDEXES KHUYẾN NGHỊ
   ========================================================= */
CREATE INDEX IX_ReturnExchange_Order_Status
    ON Return_Exchange(Order_ID, Status);

CREATE INDEX IX_ReturnExchange_User_Status
    ON Return_Exchange(User_ID, Status);

CREATE INDEX IX_ReturnExchange_RequestDate
    ON Return_Exchange(Request_Date);

CREATE INDEX IX_ReturnExchangeItem_OrderDetail
    ON Return_Exchange_Item(Order_Detail_ID);
GO

/* =========================================================
   4) FILTERED UNIQUE INDEX KHUYẾN NGHỊ
   Chặn 1 order có nhiều REFUND request mở cùng lúc
   ========================================================= */
CREATE UNIQUE INDEX UX_ReturnExchange_OpenRefund_Order
ON Return_Exchange(Order_ID, Return_Type, Request_Scope)
WHERE Return_Type = N'REFUND'
  AND Request_Scope = N'ORDER'
  AND Status IN (N'PENDING', N'APPROVED');
GO
```

---

## 6. Giải thích chi tiết từng thay đổi quan trọng

### 6.1. Vì sao bỏ `Quantity` ở header

> [CHỈNH SỬA QUAN TRỌNG]

File gốc có `Quantity` trong `Return_Exchange`. Cách này không tốt vì:

- với request item-level nhiều dòng, quantity tổng dễ lệch với detail
- với refund cấp order thì quantity không có nhiều ý nghĩa
- khi sửa item detail, header quantity dễ bị quên đồng bộ

Khuyến nghị:

- chỉ giữ `Quantity` ở `Return_Exchange_Item`
- nếu cần tổng quantity thì tính từ detail trong query/service

### 6.2. Vì sao thêm `Refund_Account_Name`

Refund thủ công thường cần:

- số tài khoản / số ví
- tên chủ tài khoản

Nếu chỉ có account number, staff vẫn có thể chuyển khoản nhưng dễ nhầm người nhận hoặc khó đối soát.

### 6.3. Vì sao thêm `Processed_By`, `Processed_Date`

`Approved_By` chỉ phản ánh người duyệt.  
Nhưng với refund thủ công, người duyệt và người thực hiện có thể:

- là cùng một staff trong giai đoạn đầu
- khác nhau khi hệ thống mở rộng

Bổ sung 2 cột này giúp audit rõ hơn.

### 6.4. Vì sao đổi tên evidence fields

File gốc dùng:

- `Image_URL`
- `Evidence_URL`

Tên này đúng về mặt kỹ thuật nhưng chưa rõ ai upload.  
Bản mới tách thành:

- `Customer_Evidence_URL`
- `Staff_Refund_Evidence_URL`

=> giảm nhầm lẫn khi code frontend/backend/API.

---

## 7. Rule cần xử lý ở service layer (DB khó enforce hoàn toàn)

### 7.1. `Order_Detail_ID` phải thuộc đúng `Order_ID`

DB hiện có:

- `Return_Exchange.Order_ID`
- `Return_Exchange_Item.Order_Detail_ID`

Nhưng không có FK trực tiếp nào đảm bảo `Order_Detail` đó thật sự thuộc đúng `Order_ID` của header.  
Rule này phải kiểm tra trong service:

```text
Order_Detail.Order_ID phải bằng Return_Exchange.Order_ID
```

### 7.2. Chống trùng request mở cho cùng item

Unique `(Return_Exchange_ID, Order_Detail_ID)` chỉ chặn trùng trong cùng một request.  
Nó không tự chặn được:

- request A đang `PENDING`
- user lại tạo request B mới cho cùng `Order_Detail`

Rule này phải chặn ở service/query.

### 7.3. Validate tổng quantity đã trả

Tổng số lượng:

```text
đã trả thành công + đang chờ xử lý <= số lượng đã mua
```

DB không dễ enforce rule này bằng constraint tĩnh; nên xử lý ở service.

---

## 8. Cách dùng schema cho từng flow

## 8.1. Flow hủy đơn trước xác nhận

`Return_Exchange`:

- `Return_Type = REFUND`
- `Request_Scope = ORDER`
- có `Refund_Amount`
- có `Refund_Method`
- có `Refund_Account_Number`
- **không cần** `Return_Exchange_Item`

## 8.2. Flow trả hàng và hoàn tiền do lỗi sản phẩm

`Return_Exchange`:

- `Return_Type = RETURN`
- `Request_Scope = ITEM`
- có `Customer_Evidence_URL`
- có thể có `Refund_Method`, `Refund_Account_Number` từ lúc customer tạo request
- refund chỉ hoàn tất khi staff complete

`Return_Exchange_Item`:

- chứa các item cụ thể cần xử lý
- mỗi dòng gắn với `Order_Detail_ID`

---

## 9. Ví dụ dữ liệu minh họa

### 9.1. Refund cấp order do hủy trước confirm

`Return_Exchange`

- `Return_Type = REFUND`
- `Request_Scope = ORDER`
- `Refund_Amount = 400000`
- `Refund_Method = BANK_TRANSFER`
- `Refund_Account_Number = 123456789`
- `Refund_Account_Name = NGUYEN VAN A`
- `Status = PENDING`

Không có dòng trong `Return_Exchange_Item`.

### 9.2. Return item lỗi sau khi giao hàng

`Return_Exchange`

- `Return_Type = RETURN`
- `Request_Scope = ITEM`
- `Customer_Evidence_URL = ...`
- `Status = PENDING`

`Return_Exchange_Item`

- dòng 1: `Order_Detail_ID = 1001`, `Quantity = 1`
- dòng 2: `Order_Detail_ID = 1003`, `Quantity = 1`

---

## 10. Khuyến nghị mở rộng trong tương lai (không bắt buộc ở giai đoạn hiện tại)

### 10.1. Tách bảng attachment riêng

Nếu sau này cần:

- nhiều ảnh/video từ CUSTOMER
- nhiều evidence từ STAFF

thì nên tạo:

- `Return_Exchange_Attachment`
- `Attachment_Type`
- `Uploaded_By_Role`

### 10.2. Thêm trạng thái trung gian

Khi hệ thống phức tạp hơn có thể mở rộng:

- `RECEIVED`
- `REFUND_PROCESSING`
- `REFUND_FAILED`

### 10.3. Tách refund transaction riêng

Nếu sau này cần lịch sử hoàn tiền nhiều lần cho 1 request, có thể tạo bảng:

- `Refund_Transaction`

Hiện tại chưa cần vì bạn đang dùng refund thủ công đơn giản và mỗi request dự kiến xử lý một lần.

---

## 11. Kết luận

Schema trong tài liệu này được thiết kế lại theo nguyên tắc:

1. Đơn giản đủ để triển khai ngay.
2. Rõ vai trò từng field.
3. Tách rõ evidence của CUSTOMER và STAFF.
4. Hỗ trợ tốt cho refund thủ công.
5. Vẫn giữ được hướng mở rộng cho tương lai.

Đây là phiên bản phù hợp hơn file gốc để bạn dùng làm tài liệu chuẩn hóa cho backend, frontend và database.
