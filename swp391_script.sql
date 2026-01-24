CREATE DATABASE EyewearManagement
GO
USE EyewearManagement
GO
CREATE TABLE Role (
                      Role_ID INT IDENTITY PRIMARY KEY,
                      Type_Name NVARCHAR(50) NOT NULL,
                      CHECK (Type_Name IN ('CUSTOMER', 'ADMIN', 'MANAGER', 'SALES STAFF', 'OPERATIONS STAFF'))
);

CREATE TABLE [User] (
                        User_ID INT IDENTITY PRIMARY KEY,
                        Username NVARCHAR(50) UNIQUE NOT NULL,
    Password NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100) UNIQUE NOT NULL,
    Phone VARCHAR(15) NOT NULL,
    Role_ID INT NOT NULL,
    Status BIT NOT NULL,
    Name NVARCHAR(100) NOT NULL,
    Address NVARCHAR(255) NOT NULL,
    Date_of_Birth DATE,
    ID_Number VARCHAR(20) UNIQUE,
    CONSTRAINT FK_User_Role FOREIGN KEY (Role_ID)
    REFERENCES Role(Role_ID),
    CONSTRAINT CK_User_Status CHECK (Status IN (0,1))
    );

CREATE TABLE Brand (
                       Brand_ID BIGINT IDENTITY PRIMARY KEY,
                       Brand_Name NVARCHAR(100) NOT NULL,
                       Description NVARCHAR(255),
                       Logo_URL VARCHAR(MAX),
    Status BIT NOT NULL
);

CREATE TABLE Supplier (
                          Supplier_ID BIGINT IDENTITY PRIMARY KEY,
                          Supplier_Name NVARCHAR(100) NOT NULL,
                          Supplier_Phone VARCHAR(15) NOT NULL,
                          Supplier_Address NVARCHAR(255) NOT NULL
);

CREATE TABLE Brand_Supplier (
                                Brand_Supplier_ID BIGINT IDENTITY PRIMARY KEY,
                                Brand_ID BIGINT NOT NULL,
                                Supplier_ID BIGINT NOT NULL,
                                CONSTRAINT FK_BrandSupplier_Brand FOREIGN KEY (Brand_ID)
                                    REFERENCES Brand(Brand_ID),
                                CONSTRAINT FK_BrandSupplier_Supplier FOREIGN KEY (Supplier_ID)
                                    REFERENCES Supplier(Supplier_ID)
);

CREATE TABLE Product_Type (
                              Product_Type_ID BIGINT IDENTITY PRIMARY KEY,
                              Type_Name NVARCHAR(100) NOT NULL,
                              Description NVARCHAR(255)
);

CREATE TABLE Product (
                         Product_ID BIGINT IDENTITY PRIMARY KEY,
                         Product_Name NVARCHAR(255) NOT NULL,
                         SKU NVARCHAR(50),
                         Product_Type_ID BIGINT NOT NULL,
                         Brand_ID BIGINT NOT NULL,
                         Price DECIMAL(15,2) NOT NULL,
                         Cost_Price DECIMAL(15,2) NOT NULL,
                         Allow_Preorder BIT NOT NULL DEFAULT 0,
                         Description NVARCHAR(500),
                         CONSTRAINT FK_Product_ProductType FOREIGN KEY (Product_Type_ID)
                             REFERENCES Product_Type(Product_Type_ID),
                         CONSTRAINT FK_Product_Brand FOREIGN KEY (Brand_ID)
                             REFERENCES Brand(Brand_ID)
);

CREATE TABLE Product_Image (
                               Image_ID BIGINT IDENTITY PRIMARY KEY,
                               Product_ID BIGINT NOT NULL,
                               Image_URL VARCHAR(MAX) NOT NULL,
    Is_Avatar BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_ProductImage_Product FOREIGN KEY (Product_ID)
        REFERENCES Product(Product_ID)
);

CREATE TABLE Inventory (
                           Inventory_ID BIGINT IDENTITY PRIMARY KEY,
                           Product_ID BIGINT NOT NULL,
                           Quantity_Before INT NOT NULL,
                           Quantity_After INT NOT NULL,
                           User_ID INT NOT NULL,
                           Supplier_ID BIGINT NOT NULL,
                           Order_Date DATETIME,
                           Received_Date DATETIME,
                           Unit NVARCHAR(20),
                           CONSTRAINT FK_Inventory_Product FOREIGN KEY (Product_ID)
                               REFERENCES Product(Product_ID),
                           CONSTRAINT FK_Inventory_User FOREIGN KEY (User_ID)
                               REFERENCES [User](User_ID),
                           CONSTRAINT FK_Inventory_Supplier FOREIGN KEY (Supplier_ID)
                               REFERENCES Supplier(Supplier_ID)
);

CREATE TABLE [Order] (
                         Order_ID BIGINT IDENTITY PRIMARY KEY,
                         User_ID INT NOT NULL,
                         Order_Code NVARCHAR(50) UNIQUE,
    Order_Date DATETIME NOT NULL DEFAULT GETDATE(),
    Sub_Total DECIMAL(15,2) NOT NULL,        -- tổng tiền hàng
    Tax_Amount DECIMAL(15,2) DEFAULT 0,      -- thuế
    Discount_Amount DECIMAL(15,2) DEFAULT 0, -- giảm giá
    Total_Amount AS (Sub_Total + Tax_Amount - Discount_Amount), -- tổng phải trả
    Order_Type NVARCHAR(20) NOT NULL,        -- ONLINE / OFFLINE / PRESCRIPTION
    Order_Status NVARCHAR(30) NOT NULL,      -- PENDING / PARTIALLY_PAID / PAID / PROCESSING / COMPLETED / CANCELED
    CONSTRAINT FK_Order_User FOREIGN KEY (User_ID)
    REFERENCES [User](User_ID),
    CONSTRAINT CK_Order_Status
    CHECK (Order_Status IN (
           N'PENDING',
           N'CONFIRMED',
           N'PARTIALLY_PAID',
           N'PAID',
           N'PROCESSING',
           N'READY',
           N'COMPLETED',
           N'CANCELED'
                           )),
    CONSTRAINT CK_Order_Type
    CHECK (Order_Type IN (
           N'DIRECT_ORDER',
           N'PRE_ORDER',
           N'PRESCRIPTION_ORDER'
                         ))
    );

CREATE TABLE Payment (
                         Payment_ID BIGINT IDENTITY PRIMARY KEY,
                         Order_ID BIGINT NOT NULL,
                         Payment_Date DATETIME NOT NULL DEFAULT GETDATE(),
                         Payment_Method NVARCHAR(50) NOT NULL,    -- CASH / BANK_TRANSFER / MOMO / VNPAY / COD
                         Amount DECIMAL(15,2) NOT NULL,           -- số tiền lần này
                         Status NVARCHAR(20) NOT NULL,            -- SUCCESS / FAILED / REFUNDED
                         CONSTRAINT FK_Payment_Order FOREIGN KEY (Order_ID)
                             REFERENCES [Order](Order_ID),
                         CONSTRAINT CK_Payment_Status
                             CHECK (Status IN (N'SUCCESS', N'FAILED', N'REFUNDED')),
                         CONSTRAINT CK_Payment_Amount
                             CHECK (Amount > 0),
                         CONSTRAINT CK_Payment_Method
                             CHECK (Payment_Method IN (
                                                       N'CASH',
                                                       N'MOMO',
                                                       N'VNPAY',
                                                       N'COD'
                                 ))
);

CREATE TABLE Invoice (
                         Invoice_ID BIGINT IDENTITY PRIMARY KEY,
                         Order_ID BIGINT NOT NULL UNIQUE,  -- đảm bảo 1–1
                         Issue_Date DATETIME NOT NULL DEFAULT GETDATE(),
                         Total_Amount DECIMAL(15,2) NOT NULL,     -- copy từ Order.Total_Amount
                         Status NVARCHAR(20) NOT NULL,            -- UNPAID / PARTIALLY_PAID / PAID / CANCELED
                         CONSTRAINT FK_Invoice_Order FOREIGN KEY (Order_ID)
                             REFERENCES [Order](Order_ID),
                         CONSTRAINT CK_Invoice_Status
                             CHECK (Status IN (
                                               N'UNPAID',
                                               N'PARTIALLY_PAID',
                                               N'PAID',
                                               N'CANCELED'
                                 ))
);

CREATE TABLE Order_Detail (
                              Order_Detail_ID BIGINT IDENTITY PRIMARY KEY,
                              Order_ID BIGINT NOT NULL,
                              Product_ID BIGINT NOT NULL,
                              Unit_Price DECIMAL(15,2) NOT NULL,
                              Note NVARCHAR(500),
                              Quantity INT NOT NULL,
                              CONSTRAINT FK_OrderDetail_Order FOREIGN KEY (Order_ID)
                                  REFERENCES [Order](Order_ID),
                              CONSTRAINT FK_OrderDetail_Product FOREIGN KEY (Product_ID)
                                  REFERENCES Product(Product_ID)
);

CREATE TABLE Order_Processing (
                                  Order_Processing_ID BIGINT IDENTITY PRIMARY KEY,
                                  Order_ID BIGINT NOT NULL,
                                  Changed_By INT NOT NULL,
                                  Changed_At DATETIME NOT NULL DEFAULT GETDATE(),
                                  Note NVARCHAR(255),
                                  CONSTRAINT FK_OrderProcessing_Order
                                      FOREIGN KEY (Order_ID)
                                          REFERENCES [Order](Order_ID),
                                  CONSTRAINT FK_OrderProcessing_User
                                      FOREIGN KEY (Changed_By)
                                          REFERENCES [User](User_ID)
);

CREATE TABLE Promotion (
                           Promotion_ID BIGINT IDENTITY PRIMARY KEY,
                           Promotion_Code NVARCHAR(50) NOT NULL,
                           Promotion_Name NVARCHAR(255) NOT NULL,
                           Promotion_Type NVARCHAR(50) NOT NULL,
                           Discount_Value DECIMAL(15,2) NOT NULL,
                           Discount_Type NVARCHAR(50) NOT NULL,
                           Start_Date DATETIME NOT NULL,
                           End_Date DATETIME NOT NULL,
                           Usage_Limit INT,
                           Used_Count INT NOT NULL,
                           Is_Active BIT NOT NULL
);

CREATE TABLE Order_Promotion (
                                 Order_Promotion_ID BIGINT IDENTITY PRIMARY KEY,
                                 Order_ID BIGINT NOT NULL,
                                 Promotion_ID BIGINT NOT NULL,
                                 Discount_Value DECIMAL(15,2),
                                 CONSTRAINT FK_OrderPromotion_Order FOREIGN KEY (Order_ID)
                                     REFERENCES [Order](Order_ID),
                                 CONSTRAINT FK_OrderPromotion_Promotion FOREIGN KEY (Promotion_ID)
                                     REFERENCES Promotion(Promotion_ID)
);

CREATE TABLE Product_Promotion (
                                   Product_Promotion_ID BIGINT IDENTITY PRIMARY KEY,
                                   Product_ID BIGINT NOT NULL,
                                   Promotion_ID BIGINT NOT NULL,
                                   Discount_Value DECIMAL(15,2),
                                   Start_Date DATETIME NOT NULL,
                                   End_Date DATETIME NOT NULL,
                                   CONSTRAINT FK_ProductPromotion_Product FOREIGN KEY (Product_ID)
                                       REFERENCES Product(Product_ID),
                                   CONSTRAINT FK_ProductPromotion_Promotion FOREIGN KEY (Promotion_ID)
                                       REFERENCES Promotion(Promotion_ID)
);

CREATE TABLE Return_Exchange (
                                 Return_Exchange_ID BIGINT IDENTITY PRIMARY KEY,
                                 Order_Detail_ID BIGINT NOT NULL,     -- dòng sản phẩm bị trả
                                 User_ID INT NOT NULL,             -- customer yêu cầu trả
                                 Return_Code NVARCHAR(50) UNIQUE NOT NULL,
                                 Request_Date DATETIME NOT NULL DEFAULT GETDATE(),
                                 Quantity INT NOT NULL,
                                 Return_Reason NVARCHAR(500),
                                 Product_Condition NVARCHAR(50),           -- NEW / USED / DAMAGED
                                 Refund_Amount DECIMAL(15,2),
                                 Refund_Method NVARCHAR(50),                -- CASH / BANK_TRANSFER / MOMO / VNPAY
                                 Refund_Account_Number NVARCHAR(50),
                                 Status NVARCHAR(30) NOT NULL,              -- PENDING / APPROVED / REJECTED / COMPLETED
                                 Approved_By INT,                           -- Staff xử lý
                                 Approved_Date DATETIME,
                                 Reject_Reason NVARCHAR(500),
                                 Image_URL NVARCHAR(500),
                                 CONSTRAINT FK_Return_OrderDetail FOREIGN KEY (Order_Detail_ID)
                                     REFERENCES Order_Detail(Order_Detail_ID),
                                 CONSTRAINT FK_Return_User FOREIGN KEY (User_ID)
                                     REFERENCES [User](User_ID),
                                 CONSTRAINT FK_Return_ApprovedBy FOREIGN KEY (Approved_By)
                                     REFERENCES [User](User_ID),
                                 CONSTRAINT CK_Return_Status
                                     CHECK (Status IN (
                                                       N'PENDING',
                                                       N'APPROVED',
                                                       N'REJECTED',
                                                       N'COMPLETED'
                                         )),
                                 CONSTRAINT CK_Return_Quantity
                                     CHECK (Quantity > 0),
                                 CONSTRAINT CK_Return_Refund_Amount
                                     CHECK (Refund_Amount IS NULL OR Refund_Amount >= 0),
                                 CONSTRAINT CK_Return_Refund_Method
                                     CHECK (Refund_Method IS NULL OR Refund_Method IN (
                                                                                       N'CASH',
                                                                                       N'MOMO',
                                                                                       N'VNPAY'
                                         )),
                                 CONSTRAINT CK_Return_Product_Condition
                                     CHECK (Product_Condition IS NULL OR Product_Condition IN (
                                                                                               N'NEW',
                                                                                               N'USED',
                                                                                               N'DAMAGED'
                                         ))
);

GO
-- Seed Role first to satisfy FK in User
INSERT INTO Role (Type_Name) VALUES
    (N'CUSTOMER'),
    (N'ADMIN'),
    (N'MANAGER'),
    (N'SALES STAFF'),
    (N'OPERATIONS STAFF');

CREATE TABLE Frame (
                       Frame_ID BIGINT IDENTITY PRIMARY KEY,
                       Product_ID BIGINT UNIQUE NOT NULL,
                       Color NVARCHAR(50),
                       Temple_Length DECIMAL(5,2),
                       Lens_Width DECIMAL(5,2),
                       Bridge_Width DECIMAL(5,2),
                       Frame_Shape_Name NVARCHAR(255),
                       Frame_Material_Name NVARCHAR(255),
                       Description NVARCHAR(255),
                       CONSTRAINT FK_Frame_Product FOREIGN KEY (Product_ID)
                           REFERENCES Product(Product_ID)
);

CREATE TABLE Lens_Type (
                           Lens_Type_ID BIGINT IDENTITY PRIMARY KEY,
                           Type_Name NVARCHAR(50) NOT NULL,
                           Description NVARCHAR(255)
);

CREATE TABLE Lens (
                      Lens_ID BIGINT IDENTITY PRIMARY KEY,
                      Product_ID BIGINT UNIQUE NOT NULL,
                      Lens_Type_ID BIGINT NOT NULL,
                      Index_Value DECIMAL(5,2),
                      Diameter DECIMAL(5,2),
                      Available_Power_Range NVARCHAR(200),
                      Is_Blue_Light_Block BIT,
                      Is_Photochromic BIT,
                      Description NVARCHAR(255),
                      CONSTRAINT FK_Lens_Product FOREIGN KEY (Product_ID)
                          REFERENCES Product(Product_ID),
                      CONSTRAINT FK_Lens_LensType FOREIGN KEY (Lens_Type_ID)
                          REFERENCES Lens_Type(Lens_Type_ID)
);

CREATE TABLE Contact_Lens (
                              Contact_Lens_ID BIGINT IDENTITY PRIMARY KEY,
                              Product_ID BIGINT UNIQUE NOT NULL,
                              Usage_Type NVARCHAR(50),
                              Base_Curve DECIMAL(5,2),
                              Diameter DECIMAL(5,2),
                              Water_Content DECIMAL(5,2),
                              Available_Power_Range NVARCHAR(200),
                              Quantity_Per_Box INT,
                              Lens_Material NVARCHAR(50),
                              Replacement_Schedule NVARCHAR(50),
                              Color NVARCHAR(50),
                              CONSTRAINT FK_ContactLens_Product FOREIGN KEY (Product_ID)
                                  REFERENCES Product(Product_ID)
);

CREATE TABLE Prescription_Order (
                                    Prescription_Order_ID BIGINT IDENTITY PRIMARY KEY,
                                    Order_ID BIGINT UNIQUE NOT NULL,
                                    User_ID INT NOT NULL,
                                    Prescription_Date DATETIME NOT NULL,
                                    Note NVARCHAR(500),
                                    Complete_Date DATE,
                                    CONSTRAINT FK_Prescription_Order FOREIGN KEY (Order_ID)
                                        REFERENCES [Order](Order_ID),
                                    CONSTRAINT FK_Prescription_User FOREIGN KEY (User_ID)
                                        REFERENCES [User](User_ID)
);

CREATE TABLE Prescription_Order_Detail (
                                           Prescription_Order_Detail_ID BIGINT IDENTITY PRIMARY KEY,
                                           Prescription_Order_ID BIGINT NOT NULL,
                                           Frame_ID BIGINT,
                                           Lens_ID BIGINT,
                                           Right_Eye_Sph DECIMAL(5,2),
                                           Right_Eye_Cyl DECIMAL(5,2),
                                           Right_Eye_Axis INT,
                                           Left_Eye_Sph DECIMAL(5,2),
                                           Left_Eye_Cyl DECIMAL(5,2),
                                           Left_Eye_Axis INT,
                                           Sub_Total DECIMAL(15,2) NOT NULL,
                                           CONSTRAINT FK_PrescriptionDetail_Order FOREIGN KEY (Prescription_Order_ID)
                                               REFERENCES Prescription_Order(Prescription_Order_ID),
                                           CONSTRAINT FK_PrescriptionDetail_Frame FOREIGN KEY (Frame_ID)
                                               REFERENCES Frame(Frame_ID),
                                           CONSTRAINT FK_PrescriptionDetail_Lens FOREIGN KEY (Lens_ID)
                                               REFERENCES Lens(Lens_ID)
);

INSERT INTO [User]
(Username, Password, Email, Phone, Role_ID, Status, Name, Address, Date_of_Birth, ID_Number)
VALUES
    (N'customer01', N'123456', 'customer01@gmail.com', '0901112222', 1, 1, N'Nguyễn Văn A', N'Quận 1, TP.HCM', '1998-05-10', '0123456789'),
    (N'annguyen', N'annguyen123', 'annguyen@gmail.com', '0123456789', 2, 1, N'Ân Nguyễn', N'Landmark 81, Quận 1, TP.HCM', '1990-02-15', '012345678901'),
    (N'huyvu', N'huyvu123', 'huyvu@gmail.com', '0123456788', 2, 1, N'Huy Vũ', N'Landmark 82, Quận 1, TP.HCM', '1980-02-15', '012345678902'),
    (N'quangnhat', N'quangnnhat123', 'quangnhat@gmail.com', '0123456787', 5, 1, N'Quang Trịnh', N'Phú Mỹ Hưng, Quận 7, TP.HCM', '2005-08-20', '012345678903'),
    (N'phatvo', N'phatvo123', 'phatvo@gmail.com', '0123456786', 3, 1, N'Phát Võ', N'Thảo Điền, Quận 9, TP.HCM', '2004-08-20', '012345678904'),
    (N'kienpham', N'kienpham123', 'kienpham@gmail.com', '0123456785', 4, 1, N'Kiên Phạm', N'Thảo Điền, Quận 9, TP.HCM', '2003-08-20', '012345678905');

INSERT INTO Brand (Brand_Name, Description, Logo_URL, Status) VALUES
                                                                  (N'Ray-Ban', N'Thương hiệu kính nổi tiếng của Mỹ', NULL, 1),
                                                                  (N'Oakley', N'Kính thể thao cao cấp', NULL, 1),
                                                                  (N'Gucci', N'Thương hiệu thời trang xa xỉ', NULL, 1),
                                                                  (N'Prada', N'Thương hiệu thời trang cao cấp', NULL, 1),
                                                                  (N'Gentle Monster', N'Thương hiệu kính Hàn Quốc', NULL, 1),
                                                                  (N'Essilor', N'Hãng tròng kính Pháp', NULL, 1),
                                                                  (N'HOYA', N'Hãng tròng kính Nhật Bản', NULL, 1),
                                                                  (N'Acuvue', N'Thương hiệu kính áp tròng', NULL, 1);

INSERT INTO Product_Type (Type_Name, Description)
VALUES
    (N'Gọng Kính', N'Gọng kính'),
    (N'Tròng Kính', N'Tròng kính'),
    (N'Kính Áp Tròng', N'Kính áp tròng');

INSERT INTO Product (Product_Name, SKU, Product_Type_ID, Brand_ID, Price, Cost_Price, Allow_Preorder, Description) VALUES
-- Frames (Gọng kính)
(N'Ray-Ban Aviator Classic RB3025', N'RB-AVI-3025', 1, 1, 4500000, 3200000, 0, N'Gọng kính phi công cổ điển, chất liệu kim loại cao cấp'),
(N'Ray-Ban Wayfarer RB2140', N'RB-WAY-2140', 1, 1, 4200000, 3000000, 0, N'Gọng kính phong cách retro'),
(N'Oakley Flak 2.0 XL', N'OAK-FLK-20XL', 1, 2, 5800000, 4100000, 1, N'Gọng kính thể thao chuyên nghiệp, siêu nhẹ'),
(N'Gucci GG0061O', N'GUC-GG0061O', 1, 3, 12000000, 8500000, 1, N'Gọng kính sang trọng với logo Gucci nổi bật'),
(N'Prada VPR 16M', N'PRA-VPR16M', 1, 4, 11500000, 8200000, 0, N'Gọng kính thời trang cao cấp, thiết kế thanh lịch'),
(N'Gentle Monster VACANCES', N'GM-VACANCES', 1, 5, 6500000, 4800000, 0, N'Gọng kính oversize phong cách Hàn Quốc'),

-- Lenses (Tròng kính)
(N'Essilor Crizal Sapphire UV', N'ESS-CRI-SAPH', 2, 6, 3200000, 2100000, 0, N'Tròng kính chống phản chiếu cao cấp, chống tia UV'),
(N'Essilor Varilux X Series', N'ESS-VAR-X', 2, 6, 8500000, 5800000, 0, N'Tròng kính đa tròng thế hệ mới, chuyển tiêu mượt mà'),
(N'HOYA BlueControl', N'HOY-BLC-001', 2, 7, 2800000, 1900000, 0, N'Tròng kính lọc ánh sáng xanh từ màn hình'),
(N'HOYA Sensity', N'HOY-SEN-001', 2, 7, 4500000, 3100000, 0, N'Tròng kính đổi màu thông minh'),

-- Contact Lenses (Kính áp tròng)
(N'Acuvue Oasys 1-Day', N'ACU-OAS-1D30', 3, 8, 450000, 320000, 0, N'Kính áp tròng ngày, hộp 30 miếng, độ ẩm cao'),
(N'Acuvue Oasys 2-Week', N'ACU-OAS-2W6', 3, 8, 580000, 410000, 0, N'Kính áp tròng 2 tuần, hộp 6 miếng, công nghệ Hydraclear Plus');

INSERT INTO Frame (Product_ID, Color, Temple_Length, Lens_Width, Bridge_Width, Frame_Shape_Name, Frame_Material_Name, Description) VALUES
                                                                                                                                       (1, N'Vàng Gold', 140.00, 58.00, 14.00, N'Tròn', N'Kim loại', N'Gọng kính phi công cổ điển màu vàng gold'),
                                                                                                                                       (2, N'Đen Bóng', 150.00, 50.00, 22.00, N'Vuông', N'Nhựa', N'Gọng kính vuông màu đen bóng phong cách retro'),
                                                                                                                                       (3, N'Đen Nhám', 133.00, 59.00, 12.00, N'Đa Giác', N'Titan', N'Gọng kính thể thao siêu nhẹ màu đen nhám'),
                                                                                                                                       (4, N'Nâu Havana', 140.00, 53.00, 18.00, N'Mắt Mèo', N'Nhựa', N'Gọng kính mắt mèo sang trọng màu nâu havana'),
                                                                                                                                       (5, N'Đỏ Burgundy', 135.00, 52.00, 17.00, N'Oval', N'Kim loại + Nhựa', N'Gọng kính oval màu đỏ burgundy thanh lịch'),
                                                                                                                                       (6, N'Trắng Trong Suốt', 145.00, 56.00, 20.00, N'Oversized', N'Nhựa', N'Gọng kính oversized trong suốt phong cách Hàn Quốc');

INSERT INTO Lens_Type (Type_Name, Description) VALUES
                                                   (N'Đơn tròng', N'Tròng kính đơn tròng (Single Vision) có một độ quang học trên toàn bề mặt, dùng cho cận/viễn/loạn ở một khoảng cách cố định.'),
                                                   (N'Đa tròng', N'Kính đa tròng (Progressive) hỗ trợ nhìn xa - trung gian - gần liền mạch, không có đường ranh, phù hợp người lão thị.'),
                                                   (N'Hai tròng', N'Kính hai tròng chia rõ hai vùng: phía trên nhìn xa, vùng bán nguyệt dưới nhìn gần; giải pháp truyền thống cho người lão thị.');

INSERT INTO Lens (Product_ID, Lens_Type_ID, Index_Value, Diameter, Available_Power_Range, Is_Blue_Light_Block, Is_Photochromic, Description) VALUES
                                                                                                                                                 (7, 1, 1.67, 65.00, N'-10.00 đến +6.00', 0, 0, N'Tròng kính đơn tròng chống phản chiếu cao cấp'),
                                                                                                                                                 (8, 2, 1.67, 65.00, N'+0.75 đến +3.50', 0, 0, N'Tròng kính đa tròng thế hệ mới'),
                                                                                                                                                 (9, 3, 1.60, 70.00, N'-8.00 đến +4.00', 1, 0, N'Tròng kính hai tròng lọc ánh sáng xanh hiệu quả'),
                                                                                                                                                 (10, 1, 1.60, 70.00, N'-8.00 đến +4.00', 0, 1, N'Tròng kính đổi màu thông minh');

INSERT INTO Contact_Lens (Product_ID, Usage_Type, Base_Curve, Diameter, Water_Content, Available_Power_Range, Quantity_Per_Box, Lens_Material, Replacement_Schedule, Color) VALUES
                                                                                                                                                                                (11, N'Thể thao', 8.50, 14.30, 38.00, N'-12.00 đến +6.00', 30, N'Senofilcon A', N'1 Ngày', N'Trong Suốt'),
                                                                                                                                                                                (12, N'Làm việc văn phòng', 8.40, 14.00, 38.00, N'-9.00 đến +6.00', 6, N'Senofilcon A', N'2 Tuần', N'Trong Suốt');