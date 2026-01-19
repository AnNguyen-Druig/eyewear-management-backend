# Eyewear Management Backend

Backend API cho hệ thống quản lý kính mắt sử dụng Spring Boot.

## Yêu cầu hệ thống

- Java 17 hoặc cao hơn
- Maven 3.6+
- SQL Server 2019 hoặc cao hơn

## Cấu hình Database

1. Tạo database tên `EyewearManagement` trong SQL Server
2. Copy file `application-example.properties` thành `application.properties`
3. Cập nhật thông tin database trong `application.properties`:
    - `spring.datasource.username`: Username SQL Server của bạn
    - `spring.datasource.password`: Password SQL Server của bạn

## Chạy ứng dụng
```bash
mvn spring-boot:run
```

## Công nghệ sử dụng

- Spring Boot
- SQL Server
- Maven

## Tác giả

- AnNguyen-Druig