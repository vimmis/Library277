package edu.sjsu.vimmi_swami.library277;

/**
 * Created by VimmiRao on 12/3/2017.
 */

public class API {

        private static final String URL= "http://10.0.2.2:8080";
        public static final String GetBooks = URL+"/getbooks";
        public static final String PostBooks = URL+"/addbooks";
        public static final String UpdateBooks = URL+"/updatebooks";
        public static final String DeleteBooks = URL+"/deletebooks";

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
