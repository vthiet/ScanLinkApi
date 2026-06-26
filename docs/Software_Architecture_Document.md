# TÀI LIỆU THIẾT KẾ KIẾN TRÚC PHẦN MỀM (SAD)

## HỆ THỐNG QUÉT VÀ QUẢN LÝ TÀI LIỆU DI ĐỘNG (SCANLINK)

**Phiên bản:** 1.0
<br>
**Chuẩn áp dụng:** ISO/IEC/IEEE 42010:2011 (Thay thế IEEE 1471-2000)
<br>
**Kiến trúc sư trưởng (Software Architect):** Võ Văn Thiệt

## 1\. GIỚI THIỆU (INTRODUCTION)

### 1.1 Mục đích (Purpose)

Tài liệu Thiết kế Kiến trúc Phần mềm (SAD) này cung cấp cái nhìn toàn cảnh về mặt kỹ thuật của hệ thống **ScanLink**. Tài liệu tập trung vào các quyết định kiến trúc cốt lõi, các mẫu thiết kế chính (Architectural Patterns), và cách hệ thống giải quyết các yêu cầu phi chức năng (như bảo mật của Firebase, hiệu năng đa luồng của Java 25, và tính toán biên OpenCV).

### 1.2 Phạm vi hệ thống (System Scope)

Hệ thống **ScanLink** là nền tảng số hóa và chia sẻ tài liệu gồm hai phần chính:

- **ScanLink Client (Mobile App):** Ứng dụng Android Native chạy Kotlin, chịu trách nhiệm xử lý ảnh biên (Edge Computing) để khử méo, lọc màu và OCR ngoại tuyến.
- **ScanLink Server (Backend Engine):** Máy chủ Spring Boot chạy Java 25 LTS, chịu trách nhiệm lưu trữ tập trung, phân quyền dựa trên định danh Firebase và cung cấp dịch vụ chia sẻ bảo mật.

### 1.3 Nguyên tắc Kiến trúc (Architectural Principles)

1.  **Separation of Concerns (SoC):** Phân tách rõ ràng giữa giao diện, nghiệp vụ và hạ tầng dữ liệu.
2.  **Offline-First (Client):** Đảm bảo các tính năng quét, chỉnh sửa và trích xuất chữ viết hoạt động trơn tru mà không cần kết nối mạng.
3.  **Stateless Backend:** Server không lưu trạng thái phiên (Session) của người dùng, sử dụng JWT Token của Firebase để xác thực trên mỗi Request.
4.  **Thread-per-Request với Java 25:** Sử dụng Virtual Threads thay vì Thread Pool truyền thống để tối ưu hóa tài nguyên I/O khi xử lý tải tệp lớn.

## 2\. KIẾN TRÚC TỔNG THỂ (SYSTEM REPRESENTATION - 4+1 VIEW)

Tài liệu này sử dụng mô hình **4+1 View Model** để mô tả kiến trúc dưới các góc nhìn khác nhau của các bên liên quan (Stakeholders).

``` mermaid
graph TD
    subgraph 4_plus_1_Views [Mô hình Kiến trúc 4+1 View]
        V1[Logical View - Góc nhìn Logic]
        V2[Process View - Góc nhìn Tiến trình]
        V3[Development View - Góc nhìn Phát triển]
        V4[Physical View - Góc nhìn Vật lý]
        V5((Scenarios / Use Cases))
        
        V5 -.-> V1
        V5 -.-> V2
        V5 -.-> V3
        V5 -.-> V4
    end

```

## 3\. GÓC NHÌN LOGIC (LOGICAL VIEW)

Góc nhìn logic mô tả cấu trúc phân rã chức năng và sơ đồ khối của hệ thống, giúp các nhà phát triển hiểu được cấu trúc phân tầng.

### 3.1 Cấu trúc Phân tầng Client (Android MVVM + Clean Architecture)

Ứng dụng di động được phân rã theo mô hình **Feature-First** phối hợp **Clean Architecture**:

``` mermaid
graph LR
    subgraph UI_Layer [Presentation Layer]
        View[Activity / Fragment]
        VM[ViewModel / UI State]
    end

    subgraph Business_Layer [Domain Layer - Pure Kotlin]
        UC[Use Cases / Interactors]
        Model[Domain Entities]
        Repo_Interface[Repository Interfaces]
    end

    subgraph Infrastructure_Layer [Data Layer]
        Repo_Impl[Repository Implementations]
        Local_DB[(Room Database)]
        Remote_API[Retrofit API Service]
        CV_Engine[OpenCV / ML Kit Engine]
    end

    View -->|Observe State| VM
    VM -->|Dispatches Intent| UC
    UC -->|Invokes| Repo_Interface
    Repo_Impl --|Implements|-> Repo_Interface
    Repo_Impl --> Local_DB
    Repo_Impl --> Remote_API
    Repo_Impl --> CV_Engine


```

### 3.2 Cấu trúc Phân tầng Server (Spring Boot MVC)

Backend được xây dựng theo mô hình **MVC (Model-View-Controller)** truyền thống để đẩy nhanh tiến độ và tương thích tối đa với hệ sinh thái Spring:

- **Controller Layer:** Tiếp nhận yêu cầu HTTP/HTTPS, thực hiện lọc bảo mật đầu vào, kiểm tra định danh Firebase JWT thông qua Spring Security.
- **Service Layer (Nghiệp vụ):** Điều phối luồng xử lý chính (tính toán dung lượng, mã hóa liên kết).
- **Repository Layer (JPA):** Giao tiếp với PostgreSQL thông qua Hibernate.

## 4\. GÓC NHÌN TIẾN TRÌNH (PROCESS VIEW)

Góc nhìn tiến trình giải quyết vấn đề đồng bộ hóa, tương tác động tại thời điểm chạy (runtime) và luồng dữ liệu của hệ thống.

### 4.1 Luồng Xử lý Đa luồng trên Java 25 (Virtual Threads)

Trong Java 25, cơ chế **Virtual Threads** (Project Loom) được kích hoạt mặc định cho máy chủ Tomcat nhúng của Spring Boot. Thay vì sử dụng mô hình Thread Pool giới hạn vật lý (Platform Threads), Java 25 sử dụng luồng ảo siêu nhẹ:

$$
    \text{Năng lực xử lý tối đa } (C) \propto \frac{\text{Virtual Threads}}{\text{Blocking I/O Waiting Time}}
$$

Khi một thiết bị di động gửi yêu cầu Upload tệp PDF lớn lên Cloud thông qua API của ScanLink:

1.  Spring Boot tạo ra một **Virtual Thread** riêng biệt cho request này.
2.  Khi tiến trình ghi tệp lên S3 hoặc lưu metadata vào PostgreSQL bị nghẽn (Blocking I/O), Platform Thread chạy bên dưới (Carrier Thread) sẽ tự động được giải phóng để phục vụ request khác, trong khi Virtual Thread tạm thời "treo" trên Heap memory mà không tốn tài nguyên CPU.
3.  Khi I/O hoàn tất, Virtual Thread được khôi phục và trả kết quả về cho Client.

<!-- end list -->

``` mermaid
sequenceDiagram
    autonumber
    participant Client as ScanLink Mobile App
    participant Carrier as Carrier Thread (CPU)
    participant Virtual as Virtual Thread (JVM Heap)
    participant S3 as AWS S3 Storage

    Client->>Carrier: POST /api/v1/documents (Upload)
    Carrier->>Virtual: Khởi tạo Virtual Thread xử lý request
    Virtual->>S3: Ghi luồng dữ liệu (I/O Blocking)
    Note over Carrier: Carrier Thread được giải phóng ngay lập tức<br/>để phục vụ Request khác
    S3-->>Virtual: Ghi tệp hoàn tất (I/O Resume)
    Carrier->>Virtual: Gán lại Carrier Thread trống để chạy tiếp
    Virtual-->>Client: Trả về HTTP 201 Created

```

## 5\. GÓC NHÌN PHÁT TRIỂN (DEVELOPMENT VIEW)

Góc nhìn phát triển định hình cách tổ chức mã nguồn thực tế và quản lý các phụ thuộc (Dependencies).

### 5.1 Quy tắc Phụ thuộc Mã nguồn (Dependency Rules)

Để giữ cho mã nguồn dễ bảo trì và kiểm thử:

- **Android Client:** Tầng domain tuyệt đối không được import bất kỳ thư viện nào liên quan đến Android SDK (ví dụ: android.content.Context). Lớp này chỉ sử dụng ngôn ngữ Kotlin thuần túy. Mọi tương tác phần cứng (Camera, File) phải thông qua interface định nghĩa tại domain/repository và được triển khai ở data.
- **Spring Boot Server:** Tuân thủ quy tắc phụ thuộc một chiều của MVC: Controller -\> Service -\> Repository. Các lớp DTO (Data Transfer Object) chỉ được sử dụng để giao tiếp ở tầng Controller, tránh đưa sâu xuống tầng Repository.

## 6\. GÓC NHÌN VẬT LÝ / TRIỂN KHAI (PHYSICAL / DEPLOYMENT VIEW)

Góc nhìn vật lý mô tả sơ đồ triển khai phần cứng, cấu hình mạng và sự phân bổ của các thành phần phần mềm trên môi trường thực tế.

``` mermaid
graph TD
    subgraph Client_Environment [Mobile Environment]
        Android[Android Smartphone <br/> API Level 26+]
    end

    subgraph Public_Network [Internet Gateway]
        LB[Reverse Proxy / Nginx & SSL TLS 1.3]
    end

    subgraph Firebase_Cloud [Google Identity Provider]
        FB[Firebase Authentication Service]
    end

    subgraph Enterprise_Cloud [Docker Containerized Environment]
        subgraph K8S [Kubernetes Cluster / Docker Swarm]
            APP_1[Spring Boot Instance 1 <br/> JDK 25 / Tomcat]
            APP_2[Spring Boot Instance 2 <br/> JDK 25 / Tomcat]
        end
        
        DB[(PostgreSQL Database)]
        S3[Object Storage <br/> AWS S3 / MinIO]
    end

    Android -->|1. Xác thực bằng SDK| FB
    Android -->|2. Gọi API kèm Token| LB
    LB -->|Cân bằng tải Round-Robin| APP_1 & APP_2
    APP_1 & APP_2 -.->|3. Xác thực Token Admin SDK| FB
    APP_1 & APP_2 -->|4. Lưu Metadata| DB
    APP_1 & APP_2 -->|5. Lưu trữ File PDF| S3

```

## 7\. CÁC QUYẾT ĐỊNH KIẾN TRÚC CHỦ CHỐT (KEY ARCHITECTURAL DECISIONS)

Dưới đây là bảng phân tích lý do kiến trúc sư lựa chọn giải pháp kỹ thuật, đánh đổi (Trade-offs) các yếu tố hệ thống:

| Thành phần lựa chọn         | Phương án thay thế cân nhắc                          | Lý do lựa chọn & Đánh đổi                                                                                                                                                                                            |
| :-------------------------: | :--------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Java 25 LTS**             | Java 17 LTS / Node.js (Express)                      | **Lý do:** Java 25 cung cấp tính năng Virtual Threads hoạt động ổn định và tối ưu hoàn toàn. Loại bỏ sự phức tạp của lập trình bất đồng bộ (Reactive Programming - WebFlux) nhưng vẫn đạt hiệu năng I/O tương đương. |
| **Firebase Auth**           | Tự xây dựng OAuth2 Server (Spring Security JWT)      | **Lý do:** Giảm thiểu rủi ro bảo mật hệ thống. Firebase đã đạt các chứng chỉ bảo mật quốc tế, hỗ trợ OTP, Google Sign-In và quản lý session đầu cuối sẵn có mà không tốn chi phí phát triển ban đầu.                 |
| **Xử lý OpenCV tại Client** | Đẩy ảnh thô lên Server để xử lý bằng Python (OpenCV) | **Lý do:** Giảm tải băng thông truyền tải cực lớn cho server. Tận dụng năng lực tính toán của chip di động (Edge Computing) để xử lý ảnh mượt mà, hỗ trợ chế độ quét offline.                                        |
| **PostgreSQL**              | MongoDB (NoSQL)                                      | **Lý do:** Tính năng phân quyền tệp (Private/Public/Account-based) của ScanLink đòi hỏi cấu trúc liên kết chặt chẽ (Relations), ràng buộc khóa ngoại (Foreign Keys) để đảm bảo không bị rò rỉ dữ liệu tài liệu.      |

**\[KẾT THÚC TÀI LIỆU THIẾT KẾ KIẾN TRÚC - SAD\]**
