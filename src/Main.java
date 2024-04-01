/*
Làm truy vấn bằng java, truy vấn trên server neosoft,
tự xây dựng các bảng với kiểu thư viện thật bao gồm Thư viện có sách,
Quản lý để quản lý thư viện thêm, xóa sách cập nhật trạng thái sách đang mượn hay chưa, Sinh viên mượn sách;
bao gồm 2 loại KEY là PRIMARY KEY và FOREIGN KEY để liên kết các bảng
 */
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.sql.ResultSet;
import java.util.Date;

public class Main {
    private static String jdbcURL = "jdbc:mysql://admin.neosoft.vn:3306/kawhfhmf_NeoSQL";
    private static String username = "kawhfhmf_neo_user";
    private static String password = "NeoSQL2009!";

    public static void Create(Connection connection, Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE SACH(ID_Sach INT PRIMARY KEY,Ten_Sach NVARCHAR(50) NOT NULL,Nam_Xuat_Ban INT, Nha_Xuat_Ban NVARCHAR(50) NOT NULL, Trang_Thai NVARCHAR(50));");
        statement.executeUpdate("CREATE TABLE SINHVIEN(MSSV INT PRIMARY KEY, Ho_Ten NVARCHAR(50) NOT NULL, Ngay_Sinh NVARCHAR(50), Gioi_Tinh NVARCHAR (10), Đia_Chi NVARCHAR(50));");
        statement.executeUpdate("CREATE TABLE MUONSACH(ID_Sach INT,MSSV INT, Ten_Sach NVARCHAR(50),Ngay_muon Date, Ngay_tra Date);");
        connection.close();
    }

    public static void Drop(Connection connection, Statement statement) throws SQLException {
        statement.executeUpdate("DROP TABLE SACH;");
        statement.executeUpdate("DROP TABLE SINHVIEN;");
        statement.executeUpdate("DROP TABLE MUONSACH;");
        connection.close();
    }

    private static void InsertBook(int Masach, String Tensach, int Namxb, String Nhaxb, String Trangthai) throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcURL, username, password);
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO SACH(ID_Sach,Ten_Sach,Nam_Xuat_Ban,Nha_Xuat_Ban,Trang_Thai) VALUES (" + Masach + ", \"" + Tensach + "\", \"" + Namxb + "\", \"" + Nhaxb + "\", \"" + Trangthai + "\");");
        connection.close();
    }

    public static void InsertStudent(int Mssv, String Hoten, String Ngaysinh, String Gioitinh, String Diachi) throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcURL, username, password);
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO SINHVIEN(MSSV,Ho_Ten,Ngay_Sinh,Gioi_Tinh,Đia_Chi) VALUES (" + Mssv + ", \"" + Hoten + "\", \"" + Ngaysinh + "\", \"" + Gioitinh + "\", \"" + Diachi + "\");");
        connection.close();
    }

    private static void InsertBorrowbook(int Masach, int Mssv) throws SQLException {
        LocalDate Ngaymuon = LocalDate.now();
        LocalDate Ngaytra = Ngaymuon.plusDays(4);
        Connection connection = DriverManager.getConnection(jdbcURL, username, password);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Ten_Sach FROM SACH WHERE ID_Sach = " + Masach);
        String Tensach = "";
        if (resultSet.next()) {
            Tensach = resultSet.getString("Ten_Sach");
        }
        statement.execute("INSERT INTO MUONSACH(ID_Sach, MSSV, Ten_Sach, Ngay_muon, Ngay_tra) VALUES (" + Masach + ", " + Mssv + ", '" + Tensach + "', '" + Ngaymuon + "', '" + Ngaytra + "')");
        connection.close();
    }

    private static boolean CheckBookexist(Connection connection, int Masach) throws SQLException {
        String querybook = "SELECT * FROM SACH WHERE ID_Sach = " + Masach;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(querybook);
        return resultSet.next(); // Trả về true nếu có sách tồn tại, false nếu không
    }

    private static boolean CheckStudentexist(Connection connection, int Mssv) throws SQLException {
        String querystudent = "SELECT * FROM SINHVIEN WHERE MSSV = " + Mssv;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(querystudent);
        return resultSet.next();
    }

    private static int CheckUpdateBook(Connection connection, int idbook) throws SQLException {
        String update = "UPDATE SACH " + "SET Trang_Thai = 'Đã Mượn' WHERE ID_Sach = " + idbook;
        Statement statement = connection.createStatement();
        return statement.executeUpdate(update);
    }

    private static boolean CheckUpdateBorrowBook(Connection connection, int IDbook) throws SQLException {
        String sql = "SELECT Trang_Thai FROM SACH WHERE ID_Sach = " + IDbook;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            String trangThai = resultSet.getString("Trang_Thai");
            return trangThai.equals("Đã Mượn");
        } else {
            // Không tìm thấy sách với IDbook
            return false;
        }
    }

    private static void UpdateBook(Connection connection, Date ngaytra) throws SQLException {
        String update = "UPDATE SACH " + "SET Trang_Thai = 'Có Sẵn' WHERE ID_Sach = ";
        Statement statement = connection.createStatement();
        statement.executeUpdate(update);
    }

    public static void main(String[] args) {
        try {
            int IDbook, IDstudent;
            int Masach, Mssv, Namxb;
            Scanner scanner = new Scanner(System.in);
            String Tensach, Nhaxb, Hoten, Gioitinh, Diachi;
            String ignore;
            String Ngaysinh, Trangthai;
            boolean flagsai;
            boolean flagtiep;
            Statement statement;
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);
            statement = connection.createStatement();
            //Drop(connection,statement);
            //Create(connection,statement);
            boolean returnmenu = false;
            do {
                int choose = 0;
                flagsai = false;
                flagtiep = false;
                System.out.println("Mời bạn chọn các tùy chỉnh sau đây !\n1.Thêm Sách\n2.Thêm Sinh Viên\n3.Hiển thị Bảng Sách\n4.Hiển thị Bảng Sinh Viên\n5.Mượn Sách Thư viện\n6.Hiển thị Bảng Danh Sách Sách được mượn\nLựa chọn của bạn là: ");
                choose = scanner.nextInt();
                if (choose == 1) {
                    System.out.println("Mời bạn nhập các thông tin sau để thêm dữ liệu !");
                    System.out.println("TABLE SACH:\n Mã sách: ");
                    Masach = scanner.nextInt();
                    ignore = scanner.nextLine();
                    System.out.println("Tên sách: ");
                    Tensach = scanner.nextLine();
                    System.out.println("Năm xuất bản: ");
                    Namxb = scanner.nextInt();
                    ignore = scanner.nextLine();
                    System.out.println("Nhà xuất bản: ");
                    Nhaxb = scanner.nextLine();
                    System.out.println("Trạng thái: ");
                    Trangthai = scanner.nextLine();
                    InsertBook(Masach, Tensach, Namxb, Nhaxb, Trangthai);
                } else if (choose == 2) {
                    System.out.println("Mời bạn nhập các thông tin sau để thêm dữ liệu !");
                    System.out.println("TABLE SINHVIEN:\n Mssv: ");
                    Mssv = scanner.nextInt();
                    ignore = scanner.nextLine();
                    System.out.println("Tên sinh viên: ");
                    Hoten = scanner.nextLine();
                    System.out.println("Ngày sinh: ");
                    Ngaysinh = scanner.nextLine();
                    System.out.println("Giới tính: ");
                    Gioitinh = scanner.nextLine();
                    System.out.println("Địa chỉ: ");
                    Diachi = scanner.nextLine();
                    InsertStudent(Mssv, Hoten, Ngaysinh, Gioitinh, Diachi);
                } else if (choose == 3) {
                    String query = "select * from SACH";
                    ResultSet rs = statement.executeQuery(query);
                    // ResultSetMetaData lấy tiêu đề cột
                    ResultSetMetaData metaData = rs.getMetaData();
                    System.out.println("Bảng danh sách " + metaData.getTableName(1));
                    // Lấy thông tin về các cột của kết quả truy vấn
                    int columnCount = metaData.getColumnCount();
                    // Xuất tiêu đề của các cột
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(metaData.getColumnName(i) + " | ");
                    }
                    System.out.println();
                    // Xuất dữ liệu từ các hàng trong kết quả truy vấn
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.print(rs.getString(i) + " | ");
                        }
                        System.out.println();
                    }
                    rs.close();
                } else if (choose == 4) {
                    String query_2 = "select * from SINHVIEN";
                    ResultSet rs_2 = statement.executeQuery(query_2);
                    // ResultSetMetaData lấy tiêu đề cột
                    ResultSetMetaData metaData_2 = rs_2.getMetaData();
                    System.out.println("Bảng danh sách " + metaData_2.getTableName(1));
                    // Lấy thông tin về các cột của kết quả truy vấn
                    int columnCount2 = metaData_2.getColumnCount();
                    // Xuất tiêu đề của các cột
                    for (int i = 1; i <= columnCount2; i++) {
                        System.out.print(metaData_2.getColumnName(i) + " | ");
                    }
                    System.out.println();
                    // Xuất dữ liệu từ các hàng trong kết quả truy vấn
                    while (rs_2.next()) {
                        for (int i = 1; i <= columnCount2; i++) {
                            System.out.print(rs_2.getString(i) + " | ");
                        }
                        System.out.println();
                    }
                    rs_2.close();
                } else if (choose == 5) {
                    System.out.println("Xin mời nhập Mã Sách");
                    IDbook = scanner.nextInt();
                    while (!CheckBookexist(connection, IDbook)) {
                        System.out.println("Mã sách không hợp lệ. Xin mời nhập lại !");
                        IDbook = scanner.nextInt();
                    }
                    System.out.println("Xin mời nhập MSSV");
                    IDstudent = scanner.nextInt();
                    ResultSet resultSet = statement.executeQuery("SELECT Ten_Sach FROM SACH WHERE ID_Sach = " + IDbook);
                    resultSet.next(); // Di chuyển con trỏ đến bản ghi đầu tiên
                    String tensach = resultSet.getString("Ten_Sach");
                    while (CheckUpdateBorrowBook(connection, IDbook)) {
                        System.out.println("Sách đã được mượn. Xin mời nhập lại Mã sách !");
                        IDbook = scanner.nextInt();
                    }
                    CheckUpdateBook(connection, IDbook);
                    InsertBorrowbook(IDbook, IDstudent);
                    System.out.println("-----Mượn Sách Hoàn Tất!!----");
                } else if (choose == 6) {
                    String query_3 = "select * from MUONSACH";
                    ResultSet rs_3 = statement.executeQuery(query_3);
                    // ResultSetMetaData lấy tiêu đề cột
                    ResultSetMetaData metaData_3 = rs_3.getMetaData();
                    System.out.println("Bảng danh sách " + metaData_3.getTableName(1));
                    // Lấy thông tin về các cột của kết quả truy vấn
                    int columnCount3 = metaData_3.getColumnCount();
                    // Xuất tiêu đề của các cột
                    for (int i = 1; i <= columnCount3; i++) {
                        System.out.print(metaData_3.getColumnName(i) + " | ");
                    }
                    System.out.println();
                    // Xuất dữ liệu từ các hàng trong kết quả truy vấn
                    while (rs_3.next()) {
                        for (int i = 1; i <= columnCount3; i++) {
                            System.out.print(rs_3.getString(i) + " | ");
                        }
                        System.out.println();
                    }
                    rs_3.close();
                } else {
                    System.out.println("Bạn đã chọn sai. Vui lòng chọn lại !");
                    flagsai = true;
                }
                boolean flag_again = false;
                do {
                    if (choose == 1 || choose == 2) {
                        flag_again = false;
                        int choosecontinue = 0;
                        System.out.println("Bạn có muốn thêm dữ liệu vào bảng khác không ?\n 1.Có \n 0.Không \nLựa chọn của bạn là: ");
                        choosecontinue = scanner.nextInt();
                        if (choosecontinue == 1) {
                            flagtiep = true;
                        } else if (choosecontinue == 0) {
                            flagtiep = false;
                        } else {
                            System.out.println("Bạn đã chọn sai. Vui lòng chọn lai !");
                            flag_again = true;
                        }
                    }
                }
                while (flag_again);
                boolean flaglai = false;
                do {
                    System.out.println("Bạn có muốn tiếp tục hoạt động khác không ?\n1.Có\n0.Không\nLựa chọn của bạn là: ");
                    int Continuechoice = scanner.nextInt();
                    if (Continuechoice == 1) {
                        returnmenu = true;
                    } else if (Continuechoice == 0) {
                        returnmenu = false;
                    } else {
                        System.out.println("Bạn đã chọn sai. Vui lòng chọn lai !");
                        flaglai = true;
                    }
                } while (flaglai);
            }
            while (flagsai || flagtiep || returnmenu);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

