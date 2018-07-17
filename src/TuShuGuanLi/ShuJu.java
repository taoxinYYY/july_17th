package TuShuGuanLi;

import java.sql.*;
import java.util.Scanner;

/**
 * Created by DELL on 2018/7/17.
 */
public class ShuJu {
    private Connection getConnection() {
        //1.加载驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //2.创建数据库连接字符串
            String dbURL = "jdbc:mysql://localhost:3306/hnb11?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            //3.建立数据库连接
            try {
                Connection connection = DriverManager.getConnection(dbURL, "root", "root");
                return connection;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



    private void close(Connection connection,Statement statement,ResultSet resultSet){
        try{
            if(resultSet != null){
                resultSet.close();
            }
            if( statement != null){
                statement.close();
            }
            if(connection != null){
                connection.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


private void insertDate(int id,String name,String publishers,String author){
    Connection connection = null;
    Statement statement =null;
    try {
        //1.创建数据库连接
        connection = getConnection();
        //2.构建添加sql语句
         String sql = "insert into Library(id,book_name,book_publishers,book_author) " +
                "values(" + id +  ",'" + name + " ','" + publishers + "','" + author + "')";
        //3.执行sql语句
          statement = connection.createStatement();
        //4.得到执行后的结果，确定是否添加成功
          int rows = statement.executeUpdate(sql);
          System.out.println("所受影响的行数为：" + rows);
        } catch (SQLException e) {
           e.printStackTrace();
          } finally {
        close(connection,statement,null);
    }
}



    private void deleteData(int id){
        Connection connection = null;
        Statement statement =null;
        try {
            //1.创建数据库连接
            connection = getConnection();
            //2.构建删除的sql语句
            String sql = "delete from Library where id="+id;
            //3.执行删除语句
            statement = connection.createStatement();
            //4.得到执行后的结果，确定是否成功
            int rows = statement.executeUpdate(sql);
            System.out.println("有" + rows + "行被删除成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection,statement,null);
        }
    }


    private void updateData(int id,String name,String publishers,String author) {
        Connection connection = null;
        Statement statement = null;
        try {
            //1.创建数据库连接
            connection = getConnection();
            //2.构建更新的sql语句
            String sql = "update Library set book_name='" + name + "',book_publishers = '" + publishers +
                    "',book_author = '" + author + "' where id=" + id;
            //3.执行sql语句
            statement = connection.createStatement();
            //4.得到执行结果
            int rows = statement.executeUpdate(sql);
            System.out.println("更新结果为：" + (rows > 0));
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection,statement,null);
        }
    }



    private  String [][] bestFindAllData(){
        String [][] datas = new String[100][5];
        Connection connection = null;
        Statement  statement = null;
        ResultSet resultSet = null;
        connection = getConnection();
        String sql = "select * from Library ";
        statement = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            int index = 0;
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String publishers = resultSet.getString(3);
                String author = resultSet.getString(4);
                datas [index] [0] = id + "";
                datas [index] [1] = name + "";
                datas [index] [2] = publishers + "";
                datas [index] [3] = author + "";
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection,statement,resultSet);
        }
        return datas;
    }


    private  void findAllDataFormatOutput(){
        String [][] datas = bestFindAllData();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" ========================================" + System.lineSeparator());
        buffer.append("|id\t\t|\tname\t\t| publishers\t\tauthor|"+System.lineSeparator());
        buffer.append(" ========================================" + System.lineSeparator());
        for (int i = 0; i < datas.length;i++){
            String [] values = datas[i];
            if(values[0] != null && values[1] != null && values[2] != null && values[3] != null){
                buffer.append(String.format(" %s\t\t  %s\t\t%s",values[0],values[1],values[2],values[3]));
                buffer.append(System.lineSeparator());
            }
        }
        System.out.println(buffer.toString());
    }



    private void findAccountDataById(int id){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        //1.获取数据库连接
        connection = getConnection();
        //2.构建查询的sql语句
        String  sql = "select * from Library where id=" + id;
        try {
            //3.执行sql语句，并获得结果
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            //4.遍历结果集，输出每条记录的信息
            StringBuffer buffer = new StringBuffer();
            buffer.append("=========================================" + System.lineSeparator());
            buffer.append("id\t\tname\t\tpublishers\t\tuthor"+System.lineSeparator());
            buffer.append("=========================================" + System.lineSeparator());
            while (resultSet.next()) {
                id = resultSet.getInt("id");
                String name = resultSet.getString("book_name");
                String publishers = resultSet.getString("book_publishers");
                String author = resultSet.getString("book_author") ;
                buffer.append(id + " \t|\t " + name + " \t|\t " + publishers + " \t|   " + author + System.lineSeparator());
            }
            System.out.println(buffer.toString());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }finally {
            close(connection,statement,resultSet);
        }
    }



    /**
     * 模糊搜索数据，根据用户输入的关键词来模糊查询
     * @param KeyWord
     **/
    private void findAccountDataLikeKey(String KeyWord){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        //account,password
        //1.获取数据库连接
        connection = getConnection();
        //2.构建查询的sql语句
        String sql = "select id ,book_name,book_publishers,book_author from Library  " +
                "where id like '%"+ KeyWord +"%' or book_name like '%"+ KeyWord +"%' or book_publishers like '%"+
                KeyWord + "%' or book_author like '%"+ KeyWord +"%'";
        statement = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            StringBuffer buffer = new StringBuffer();
            buffer.append("==========================================================" + System.lineSeparator());
            buffer.append("     id\t\t\tname\t\t\tpublishers\t\t\tauthor"+System.lineSeparator());
            buffer.append("==========================================================" + System.lineSeparator());
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("book_name");
                String publishers = resultSet.getString("book_publishers");
                String author = resultSet.getString("book_author");
                buffer.append(id + " \t| " + name + " \t| " + publishers + " \t| " + author + "\t|"+ System.lineSeparator());
            }
            System.out.println(buffer.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close(connection,statement,resultSet);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ShuJu sj=new ShuJu();
      while(true) {
            System.out.println("=============================================================");
            System.out.println("|******************** 欢迎使用HNB智能系统 ********************｜");
            System.out.println("| 1.添加数据   2.修改数据   3.删除数据   4.查询数据   5退出系统｜");
            System.out.println("=============================================================");
            System.out.println("====================请选择你要进行的操作======================");
            int select = 0;//接收用户选择的选项
            select = scanner.nextInt();
            while (select < 1 || select > 4) {
                System.out.println("选择的操作不能识别，请重新选择：");
                select = scanner.nextInt();
            }
            String value = null;
            if (select == 1) {
                System.out.println("请输入要添加的书籍名称、出版社和作者，中间用逗号分隔。");
                System.out.println("例：daodejing，12，laozi");
                value = scanner.next();
                String[] values = value.split(",");
                sj.insertDate((int) System.currentTimeMillis(), values[0], values[1],values[2]);
                System.out.println("是否继续加入？");
                System.out.println("1.继续     2.取消");
                int count=Integer.parseInt(scanner.next());;
                while(count == 1){
                    System.out.println("请输入要添加的书籍名称、出版社和作者，中间用逗号分隔。");
                    value = scanner.next();
                    values = value.split(",");
                    sj.insertDate((int) System.currentTimeMillis(), values[0], values[1],values[2]);
                    System.out.println("是否继续加入？");
                    System.out.println("1.继续     2.取消");
                    value = scanner.next();
                }
            } else if (select == 2) {//修改数据
                System.out.println("请输入要修改的id、账号和密码。用逗号分隔。");
                value = scanner.next();
                String[] values = value.split(",");
                sj.updateData(Integer.parseInt(values[0]),
                        values[1],values[2],values[3]);
                System.out.println("是否继续修改？");
                System.out.println("1.继续     2.取消");
                int count = Integer.parseInt(scanner.next());
                while(count == 1){
                    System.out.println("请输入要修改的id、账号和密码。用逗号分隔。");
                    value = scanner.next();
                    String[] Updatevalues = value.split(",");
                    sj.updateData(Integer.parseInt(values[0]),
                            values[1],values[2],values[3]);
                    System.out.println("是否继续修改？");
                    System.out.println("1.继续     2.取消");
                    int counts = Integer.parseInt(scanner.next());
                }

            } else if (select == 3) {//删除数据
                System.out.println("请输入要删除的id。");
                value = scanner.next();
                sj.deleteData(Integer.parseInt(value));
                System.out.println("是否继续删除？");
                System.out.println("1.继续     2.取消");
                int count = Integer.parseInt(scanner.next());
                while(count == 1){
                    System.out.println("请输入要删除的id。");
                    value = scanner.next();
                    sj.deleteData(Integer.parseInt(value));
                    System.out.println("是否继续删除？");
                    System.out.println("1.继续     2.取消");
                    count =  Integer.parseInt(scanner.next());
                }
            } else if (select == 4) {//查询数据
                System.out.println("请选择查询的方法：1.查询全部数据  2.按id查询  3.查询关键字");
                int count = Integer.parseInt(scanner.next());
                while (count>4 || count<1){
                    System.out.println("输入有误,请重新输入!");
                    count = Integer.parseInt(scanner.next());
                }
                if (count == 1) {
                    sj.findAllDataFormatOutput();
                } else if (count == 2) {
                    System.out.println("请输入要查询的id：");
                    int idNmber = Integer.parseInt(scanner.next());
                    sj.findAccountDataById(idNmber);
                } else if (count == 3) {
                    System.out.println("请输入查询的关键字：");
                    String values = scanner.next();
                    sj.findAccountDataLikeKey(values);
                }
                System.out.println("是否继续查询？");
                System.out.println("1.继续     2.取消");
                int num = Integer.parseInt(scanner.next());
                while (num == 1) {
                    System.out.println("请选择查询的方法：1.查询全部数据  2.按id查询  3.查询关键字");
                    count = Integer.parseInt(scanner.next());
                    while (count>4 || count<1){
                        System.out.println("输入有误,请重新输入!");
                    }
                    if (count == 1) {
                        sj.findAllDataFormatOutput();
                    } else if (count == 2) {
                        System.out.println("请输入要查询的id：");
                        int idNmber = Integer.parseInt(scanner.next());
                        sj.findAccountDataById(idNmber);
                    } else if (count == 3) {
                        System.out.println("请输入查询的关键字：");
                        String values = scanner.next();
                        sj.findAccountDataLikeKey(values);
                    }
                    System.out.println("是否继续查询？");
                    System.out.println("1.继续     2.取消");
                    num = Integer.parseInt(scanner.next());
                }
            }else if (select == 5){//退出系统
                System.out.println("退出成功！");
                System.exit(-1);
            }

        }
    }
}