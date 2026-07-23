# ỨNG DỤNG QUẢN LÝ CÂU LẠC BỘ

## 1. Mô tả chương trình

Ứng dụng Quản lý câu lạc bộ là chương trình Java Desktop được xây dựng bằng Java Swing.

Chương trình hỗ trợ quản lý ba nhóm dữ liệu chính:

- Thành viên câu lạc bộ.
- Sự kiện của câu lạc bộ.
- Đăng ký thành viên tham gia sự kiện.

Các chức năng chính gồm:

- Hiển thị danh sách thành viên.
- Thêm, sửa, xóa và tìm kiếm thành viên.
- Quản lý thành viên thường và thành viên ban chủ nhiệm.
- Hiển thị danh sách sự kiện.
- Thêm, sửa, xóa và tìm kiếm sự kiện.
- Đăng ký thành viên tham gia sự kiện.
- Cập nhật trạng thái tham gia.
- Xóa và tìm kiếm lượt đăng ký.
- Kiểm tra dữ liệu đầu vào.
- Không cho phép đăng ký trùng.
- Không cho thành viên ngừng hoạt động đăng ký.
- Không cho đăng ký vượt quá số lượng tối đa của sự kiện.

Mã nguồn được tổ chức theo kiến trúc MVC đơn giản và được chia thành các package:

- `model`: chứa các lớp biểu diễn dữ liệu.
- `view`: chứa các lớp giao diện Java Swing.
- `controller`: tiếp nhận và điều phối thao tác từ giao diện.
- `service`: xử lý nghiệp vụ và kiểm tra dữ liệu.
- `repository`: quản lý dữ liệu và đọc ghi file CSV.
- `exception`: chứa các ngoại lệ tự định nghĩa.
- `util`: chứa các phương thức tiện ích dùng chung.

Dữ liệu chính được quản lý bằng Java Collection Framework:

- `ArrayList<Member>`
- `ArrayList<Event>`
- `ArrayList<Registration>`

Dữ liệu được lưu trong ba file CSV:

```text
data/members.csv
data/events.csv
data/registrations.csv
```

## 2. Hướng dẫn chạy chương trình

### 2.1. Yêu cầu môi trường

Máy tính cần cài đặt:

- JDK 8 trở lên.
- NetBeans IDE.
- Apache Ant được tích hợp trong NetBeans.

Lớp chứa phương thức `main` của chương trình là:

```text
clubmanagement.ClubManagement
```

### 2.2. Chạy chương trình bằng NetBeans

Thực hiện theo các bước sau:

1. Mở NetBeans.
2. Chọn `File`.
3. Chọn `Open Project`.
4. Chọn thư mục project `ClubManagement`.
5. Bấm `Open Project`.
6. Nhấp chuột phải vào project trong cửa sổ Projects.
7. Chọn `Clean and Build`.
8. Chờ cửa sổ Output hiển thị:

```text
BUILD SUCCESSFUL
```

9. Nhấp chuột phải vào project.
10. Chọn `Run`.

Có thể nhấn phím `F6` để chạy project.

Nếu NetBeans yêu cầu chọn lớp chạy chính:

1. Nhấp chuột phải vào project.
2. Chọn `Properties`.
3. Chọn mục `Run`.
4. Tại `Main Class`, chọn:

```text
clubmanagement.ClubManagement
```

5. Bấm `OK`.
6. Chạy lại project.

### 2.3. Chạy chương trình bằng file JAR

Sau khi thực hiện `Clean and Build`, NetBeans tạo file JAR tại:

```text
dist/ClubManagement.jar
```

Mở Command Prompt hoặc PowerShell tại thư mục gốc của project.

Chạy lệnh:

```bash
java -jar dist/ClubManagement.jar
```

Cần chạy lệnh từ thư mục gốc của project để chương trình tìm thấy thư mục `data`.

Cấu trúc thư mục khi chạy:

```text
ClubManagement
├── data
│   ├── members.csv
│   ├── events.csv
│   └── registrations.csv
├── dist
│   └── ClubManagement.jar
├── src
├── build.xml
├── manifest.mf
└── README.md
```

### 2.4. Kiểm tra sau khi chạy

Sau khi chương trình khởi động, cửa sổ chính phải hiển thị ba tab:

- Thành viên.
- Sự kiện.
- Đăng ký tham gia.

Kiểm tra các chức năng:

- Hiển thị danh sách thành viên.
- Hiển thị danh sách sự kiện.
- Hiển thị danh sách đăng ký.
- Thêm, sửa, xóa và tìm kiếm dữ liệu.
- Đóng rồi mở lại chương trình để kiểm tra dữ liệu CSV vẫn được lưu.
