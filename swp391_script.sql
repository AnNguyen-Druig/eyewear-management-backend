CREATE DATABASE EyewearManagement
GO
USE EyewearManagement
GO
CREATE TABLE Role (
    Role_ID INT IDENTITY PRIMARY KEY,
    Type_Name NVARCHAR(50) NOT NULL, CHECK (Type_Name IN ('CUSTOMER', 'ADMIN', 'MANAGER', 'SALES STAFF', 'OPERATIONS STAFF'))
);

CREATE TABLE [User] (
    User_ID INT IDENTITY PRIMARY KEY,
    Username NVARCHAR(50) UNIQUE NOT NULL,
    Password NVARCHAR(100) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Phone VARCHAR(15) NOT NULL,
    Role_ID INT NOT NULL,
    Status BIT NOT NULL,
    Name NVARCHAR(100) NOT NULL,
    Address NVARCHAR(255) NOT NULL,
    Date_of_Birth DATE,
    ID_Number NVARCHAR(20) UNIQUE,
    CONSTRAINT FK_User_Role FOREIGN KEY (Role_ID)
        REFERENCES Role(Role_ID),
	CONSTRAINT CK_User_Status CHECK (Status IN (0,1))
);

CREATE TABLE Brand (
    Brand_ID INT IDENTITY PRIMARY KEY,
    Brand_Name NVARCHAR(100) NOT NULL,
    Description NVARCHAR(255),
    Logo_URL NVARCHAR(500),
    Status BIT NOT NULL
);

CREATE TABLE Supplier (
    Supplier_ID INT IDENTITY PRIMARY KEY,
    Supplier_Name NVARCHAR(100) NOT NULL,
    Supplier_Phone VARCHAR(15) NOT NULL,
    Supplier_Address NVARCHAR(255) NOT NULL
);

CREATE TABLE Brand_Supplier (
    Brand_Supplier_ID INT IDENTITY PRIMARY KEY,
    Brand_ID INT NOT NULL,
    Supplier_ID INT NOT NULL,
    CONSTRAINT FK_BrandSupplier_Brand FOREIGN KEY (Brand_ID)
        REFERENCES Brand(Brand_ID),
    CONSTRAINT FK_BrandSupplier_Supplier FOREIGN KEY (Supplier_ID)
        REFERENCES Supplier(Supplier_ID)
);

CREATE TABLE Product_Type (
    Product_Type_ID INT IDENTITY PRIMARY KEY,
    Type_Name NVARCHAR(100) NOT NULL,
    Description NVARCHAR(255)
);

CREATE TABLE Product (
    Product_ID INT IDENTITY PRIMARY KEY,
    Product_Name NVARCHAR(255) NOT NULL,
    Product_Type_ID INT NOT NULL,
    Brand_ID INT NOT NULL,
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
    Image_ID INT IDENTITY PRIMARY KEY,
    Product_ID INT NOT NULL,
    Image_URL NVARCHAR(500) NOT NULL,
    CONSTRAINT FK_ProductImage_Product FOREIGN KEY (Product_ID)
        REFERENCES Product(Product_ID)
);

CREATE TABLE Inventory (
    Inventory_ID INT IDENTITY PRIMARY KEY,
    Product_ID INT NOT NULL,
    Quantity_Before INT NOT NULL,
    Quantity_After INT NOT NULL,
    User_ID INT NOT NULL,
    Supplier_ID INT NOT NULL,
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
    Order_ID INT IDENTITY PRIMARY KEY,
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
    Payment_ID INT IDENTITY PRIMARY KEY,
    Order_ID INT NOT NULL,
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
    Invoice_ID INT IDENTITY PRIMARY KEY,
    Order_ID INT NOT NULL UNIQUE,  -- đảm bảo 1–1
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
    Order_Detail_ID INT IDENTITY PRIMARY KEY,
    Order_ID INT NOT NULL,
    Product_ID INT NOT NULL,
    Unit_Price DECIMAL(15,2) NOT NULL,
    Note NVARCHAR(500),
    Quantity INT NOT NULL,
    CONSTRAINT FK_OrderDetail_Order FOREIGN KEY (Order_ID)
        REFERENCES [Order](Order_ID),
    CONSTRAINT FK_OrderDetail_Product FOREIGN KEY (Product_ID)
        REFERENCES Product(Product_ID)
);

CREATE TABLE Order_Processing (
    Order_Processing_ID INT IDENTITY PRIMARY KEY,
    Order_ID INT NOT NULL,
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
    Promotion_ID INT IDENTITY PRIMARY KEY,
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
    Order_Promotion_ID INT IDENTITY PRIMARY KEY,
    Order_ID INT NOT NULL,
    Promotion_ID INT NOT NULL,
    Discount_Value DECIMAL(15,2),
    CONSTRAINT FK_OrderPromotion_Order FOREIGN KEY (Order_ID)
        REFERENCES [Order](Order_ID),
    CONSTRAINT FK_OrderPromotion_Promotion FOREIGN KEY (Promotion_ID)
        REFERENCES Promotion(Promotion_ID)
);

CREATE TABLE Product_Promotion (
    Product_Promotion_ID INT IDENTITY PRIMARY KEY,
    Product_ID INT NOT NULL,
    Promotion_ID INT NOT NULL,
    Discount_Value DECIMAL(15,2),
    Start_Date DATETIME NOT NULL,
    End_Date DATETIME NOT NULL,
    CONSTRAINT FK_ProductPromotion_Product FOREIGN KEY (Product_ID)
        REFERENCES Product(Product_ID),
    CONSTRAINT FK_ProductPromotion_Promotion FOREIGN KEY (Promotion_ID)
        REFERENCES Promotion(Promotion_ID)
);

CREATE TABLE Return_Exchange (
    Return_Exchange_ID INT IDENTITY PRIMARY KEY,
    Order_Detail_ID INT NOT NULL,     -- dòng sản phẩm bị trả
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

CREATE TABLE Frame (
    Frame_ID INT IDENTITY PRIMARY KEY,
    Product_ID INT UNIQUE NOT NULL,
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
    Lens_Type_ID INT IDENTITY PRIMARY KEY,
    Type_Name NVARCHAR(50) NOT NULL,
    Description NVARCHAR(255)
);

CREATE TABLE Lens (
    Lens_ID INT IDENTITY PRIMARY KEY,
    Product_ID INT UNIQUE NOT NULL,
    Lens_Type_ID INT NOT NULL,
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
    Contact_Lens_ID INT IDENTITY PRIMARY KEY,
    Product_ID INT UNIQUE NOT NULL,
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
    Prescription_Order_ID INT IDENTITY PRIMARY KEY,
    Order_ID INT UNIQUE NOT NULL,
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
    Prescription_Order_Detail_ID INT IDENTITY PRIMARY KEY,
    Prescription_Order_ID INT NOT NULL,
    Frame_ID INT,
    Lens_ID INT,
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