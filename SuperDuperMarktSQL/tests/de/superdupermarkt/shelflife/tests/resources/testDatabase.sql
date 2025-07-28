CREATE TABLE ProductType (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL UNIQUE,
    class VARCHAR(255) NOT NULL
) ENGINE = InnoDB CHARACTER SET utf8mb4;

CREATE TABLE Product (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    day DATE NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    productType_id INT(10) NOT NULL REFERENCES ProductType(id),
    quality INT(11) NOT NULL,
    basePrice DECIMAL(10, 2) NOT NULL,
    dueDate DATE NOT NULL
) ENGINE = InnoDB CHARACTER SET utf8mb4;

-- create test entries
INSERT INTO ProductType (`name`, class) VALUES
("general", "GeneralProduct"),
("error", "X"),
("cheese", "Cheese"),
("error2", "Product"),
("wine", "Wine"),
("error3", "InvalidProductType"),
("newyear", "NewYearsEve");

INSERT INTO Product (day, `name`, productType_id, quality, basePrice, dueDate) VALUES
("2024-12-01", "Brot", (SELECT id FROM ProductType WHERE name = "general"), 0, 1.15, "2024-12-05"),
("2024-12-01", "Brot", (SELECT id FROM ProductType WHERE name = "general"), 0, 1.15, "2024-12-09"),
("2024-12-01", "Salami", (SELECT id FROM ProductType WHERE name = "general"), 0, 1.70, "2024-12-30"),
("2024-12-01", "Salami", (SELECT id FROM ProductType WHERE name = "general"), 2, 1.70, "2024-12-30"),
("2024-12-01", "Emmentaler", (SELECT id FROM ProductType WHERE name = "cheese"), 35, 2.20, "2024-12-22"),
("2024-12-01", "Gouda", (SELECT id FROM ProductType WHERE name = "cheese"), 100, 2.20, "2024-12-22"),
("2024-12-01", "Brie", (SELECT id FROM ProductType WHERE name = "cheese"), 20, 2.50, "2025-01-10"),
("2024-12-01", "Weisswein", (SELECT id FROM ProductType WHERE name = "wine"), 0, 5.00, "2024-12-15"),
("2024-12-01", "Rotwein", (SELECT id FROM ProductType WHERE name = "wine"), 10, 5.00, "2025-01-01"),
("2024-12-01", "Feuerwerk", (SELECT id FROM ProductType WHERE name = "newyear"), 18, 20.00, "2025-01-15");