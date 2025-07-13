-- Create the database
CREATE
DATABASE InventoryManagement;
GO

USE InventoryManagement;
GO


CREATE TABLE Suppliers
(
    SupplierID int IDENTITY PRIMARY KEY,
    Name nvarchar(100) NOT NULL,
    Email nvarchar(100) NOT NULL,
    Phone nvarchar(20) NOT NULL,
    DeliveryTimeDays int NOT NULL,
    UNIQUE (Email)
);


CREATE TABLE Products
(
    ProductID int IDENTITY PRIMARY KEY,
    Name nvarchar(100) NOT NULL,
    CurrentStock int DEFAULT 0,
    ReorderThreshold int DEFAULT 10,
    UnitPrice decimal(10, 2) NOT NULL,
    SupplierID int,
    CHECK (CurrentStock >= 0),
    CONSTRAINT FK_Products_Supplier FOREIGN KEY (SupplierID) REFERENCES Suppliers (SupplierID)
);


CREATE TABLE PurchaseOrders
(
    OrderID int IDENTITY PRIMARY KEY,
    SupplierID int,
    CreatedAt  datetime DEFAULT GETDATE(),
    IsDelivered bit DEFAULT 0,
    isDeleted bit DEFAULT 0,
    CONSTRAINT FK_PurchaseOrders_Supplier FOREIGN KEY (SupplierID) REFERENCES Suppliers (SupplierID)
);


CREATE TABLE OrderItems
(
    OrderItemID int IDENTITY,
    OrderID int,
    ProductID int,
    UnitPrice decimal(10, 2) NOT NULL,
    Quantity int NOT NULL,
    PRIMARY KEY (OrderItemID),
    CONSTRAINT UQ_OrderItem_Order UNIQUE (ProductID, OrderID),
    CONSTRAINT FK_OrderItems_Order FOREIGN KEY (OrderID) REFERENCES PurchaseOrders (OrderID),
    CONSTRAINT FK_OrderItems_Product FOREIGN KEY (ProductID) REFERENCES Products (ProductID)
);

CREATE INDEX IX_Products_SupplierID ON Products (SupplierID);
CREATE INDEX IX_OrderItems_OrderID ON OrderItems (OrderID);
CREATE INDEX IX_OrderItems_ProductID ON OrderItems (ProductID);
GO


INSERT INTO Suppliers (Name, Email, Phone, DeliveryTimeDays)
VALUES
    ('Fresh Produce Co.', 'fresh@produce.com', '555-100-1001', 2),
    ('Quality Meats Ltd.', 'sales@qualitymeats.com', '555-200-2002', 3),
    ('Dairy Delights', 'orders@dairydelights.com', '555-300-3003', 1),
    ('Pantry Essentials', 'contact@pantryessentials.com', '555-400-4004', 5),
    ('Beverage Distributors', 'info@beveragedist.com', '555-500-5005', 4);
GO


INSERT INTO Products (Name, CurrentStock, ReorderThreshold, UnitPrice, SupplierID)
VALUES
    -- (Supplier 1)
    ('Apples', 20, 20, 0.75, 1),
    ('Bananas', 30, 30, 0.50, 1),
    ('Lettuce', 20, 15, 1.25, 1),

    -- (Supplier 2)
    ('Chicken Breast', 20, 10, 5.99, 2),
    ('Ground Beef', 20, 10, 6.49, 2),

    -- Dairy (Supplier 3)
    ('Milk (1 gallon)', 20, 15, 3.49, 3),
    ('Cheddar Cheese', 24, 12, 4.99, 3),

    -- (Supplier 4)
    ('Flour (5lb)', 10, 5, 2.99, 4),
    ('Sugar (4lb)', 10, 5, 3.49, 4),

    -- (Supplier 5)
    ('Orange Juice (64oz)', 16, 8, 4.25, 5),
    ('Bottled Water (24pk)', 20, 10, 5.99, 5);
GO
