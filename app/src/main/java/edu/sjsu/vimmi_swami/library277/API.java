package edu.sjsu.vimmi_swami.library277;

/**
 * Created by VimmiRao on 12/3/2017.
 */

public class API {

        private static final String URL= "http://10.0.2.2:8080";
        public static final String GetBooks = URL+"/api/getbooks";
        public static final String PostBooks = URL+"/api/addbooks";
        public static final String UpdateBooks = URL+"/api/updatebooks";
        public static final String DeleteBooks = URL+"/api/deletebooks";
        public static final String CheckoutBooks = URL+"/api/borrow";
        public static final String ReturnBooks = URL+"/api/returns";
        public static final String GetBorrowedBooks = URL+"/api/getBooksBorrowedBy";

        public static String registerURL(){
                return URL + "/register";
        }

        public static String logInURL(){
                return URL + "/login";
        }

        public static String confirmURL(){
                return URL + "/confirm";
        }
        public static String resendTokenURL(){
                return URL + "/resendToken";
        }
}
