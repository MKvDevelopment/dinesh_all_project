package com.typingwork.admintypingwork.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.typingwork.admintypingwork.Activity.MainActivity;
import com.typingwork.admintypingwork.R;

import java.util.List;
import java.util.Random;

public class Constant {

    public static String ADS_URL = "https://play.google.com/store/apps/details?id=com.socialmediasaver.status";

    public static String randomRobotCodeGererate() {
        //Captcha code for user input

        String LETTERS = "abcdefghijkmnopqrstuvwxy";
        String SYMBOL = "Ilk&%{@£:,-!/'$¥~>`^§]¢©";

        char[] Free_Plan_CODE = (("0123456789") + SYMBOL + LETTERS.toLowerCase() + LETTERS.toUpperCase()).toCharArray();
        // char[] Free_Plan_CODE = ("0123456789").toCharArray();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 1; i++) {
            result.append(Free_Plan_CODE[new Random().nextInt(Free_Plan_CODE.length)]);
        }
        return result.toString();
    }

    public static String generateRandomUserMarks() {
        //random no generated for passing marks
        int max = 500;
        int min = 50;
        //generate random values from 50-500
        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
        return String.valueOf(random_int);
    }

    public static int randomGererate() {
        //random no to pik name from array
        int max = 1050;
        int min = 0;
        //generate random values from 50-500
        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
        return random_int;
    }

    public static boolean isConnectionAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null) {
            return netInfo.isConnected() || netInfo.isConnectedOrConnecting() || netInfo.isAvailable();
        }
        return false;

    }

    public static void showProgressDialog(ProgressDialog progressDialog, String title, String msg) {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.setIcon(R.mipmap.ic_logo);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public static void showAlertdialog(Activity activity, AlertDialog.Builder builder, String title, String msg) {
        builder.setIcon(R.mipmap.ic_logo);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(msg);
        AlertDialog alert = builder.create();
        alert.show();
   /*     Button nbutton = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
        nbutton.setTextColor(activity.getResources().getColor(R.color.primary_dark));
        Button pbutton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        pbutton.setTextColor(activity.getResources().getColor(R.color.primary_dark));*/
    }


    public static boolean isPackageExisted(String targetPackage, Context context) {
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }

    public static String getTimeAgo(long time) {
        final long diff = System.currentTimeMillis() - time;

        if (diff < 1) {
            return " just now";
        }
        if (diff < 60 * 1000) {
            if (diff / 1000 < 2) {
                return diff / 1000 + " second ago";
            } else {
                return diff / 1000 + " seconds ago";
            }
        } else if (diff < 60 * (60 * 1000)) {
            if (diff / (60 * 1000) < 2) {
                return diff / (60 * 1000) + " minute ago";
            } else {
                return diff / (60 * 1000) + " minutes ago";
            }
        } else if (diff < 24 * (60 * (60 * 1000))) {
            if (diff / (60 * (60 * 1000)) < 2) {
                return diff / (60 * (60 * 1000)) + " hour ago";
            } else {
                return diff / (60 * (60 * 1000)) + " hours ago";
            }
        } else {
            if (diff / (24 * (60 * (60 * 1000))) < 2) {
                return diff / (24 * (60 * (60 * 1000))) + " day ago";
            } else {
                return diff / (24 * (60 * (60 * 1000))) + " days ago";
            }
        }
    }

    public static String[] userName = {"Anjani", "Anjika", "Anjini", "Anju", "Anjum", "Anjushri", "Ankaiyarkanni", "Ankal", "Ankamalika", "Ankita", "Ashalata", "Ashani", "Ashavari", "Ashika", "Ashima", "Ashira", "Ashis", "Ashita", "Ashlesha", "Ashleshakumari", "Ashmita", "Ashnaa", "Ashoka", "Ashritha", "Ashu", "Ashwabha", "Ashwini",
            "Atulya", "Auhna", "Avaapya", "Avajitha", "Avalikumari", "Avani", "Avanija", "Avanti", "Avantika", "Avasa", "Avathansha", "Avathara", "Avathi", "Avibhajya", "Aadhya", "Aanya", "Aarna", "Advika", "Bhavna", "Brinda", "Binita", "Chhaya", "Chakra", "Chara", "Daksha", "Dhriti", "Darika", "Ekaja", "Ela", "Estaa", "Eshika",
            "Eshana", "Eliya", "Forum", "Falak", "Geetika", "Gayathri", "Gulika", "Hiral", "Hemangini", "Hemal", "Ishani", "Idika", "Jeevika", "Jiera", "Kashvi", "Krisha", "Kalki ", "Laasya", "Lekha", "Mihika", "Maira", "Nyra", "Oorvi", "Oishi ", "Pihu", "Prisha", "Ridhi", "Rabhya", "Saira", "Shravya", "Trivia", "Yashica", "Bhavika",
            "Bhavini", "Bhavitha", "Bhavna", "Bhavya", "Bhikshaka", "Bhilangana", "Bhimeswari", "Bhinumathi", "Bhoomi", "Bhoomika", "Bhrahmani", "Bhramara", "Bhramari", "Bhuhathi", "Bhujatha", "Bhumija", "Bhumika", "Bhupali", "Chitramaya", "Chitrangada", "Chitrani", "Chitra", "Chitrarathi", "Chitrarekha", "Chitrita", "Chumban",
            "Chandanika", "Chandika", "Chandni", "Chandrabali", "Chandrabhaga", "Chandrabindu", "Chandraja", "Chetana", "Chhabi", "Chhavvi", "Chhaya", "Dakshata", "Daksha", "Devasree", "Devi", "Devika", "Devina", "Devya", "Devyani", "Debanshi", "Deeba", "Deepa", "Deepabali", "Deepali", "Deepamala", "Deepana", "Deepanwita",
            "Deepaprabha", "Deepashikha", "Deepavali", "Deepika", "Deepitha", "Deepta", "Deepti", "Deeptikana", "Deeptimoyee", "Deshna", "Devahuti", "Devakanya", "Devak", "Devaki", "Gauravi", "Gauri", "Gaurideepa", "Gauriganga", "Gaurija", "Gaurika", "Gauripranaya", "Gauripriya", "Gautami", "Gayana", "Gayanthika", "Gayatri", "Heera",
            "Heerkani", "Hema", "Hemadri", "Hemakshi", "Hemal", "Hemalata", "Hemamalini", "Hemangi", "Hemangini", "Hetal", "Hilla", "Hima", "Himabindhu", "Himachala", "Himagouri", "Himaja", "Himani", "Himanshu", "Harsha", "Harshada", "Harshal", "Harshavardhini", "Harshi", "Harshika", "Harshini", "Harshita", "Harshitha", "Harsika",
            "Hasamathi", "Hasanthi", "Hasika", "Hasina", "Hasita", "Hasna", "Hasumati", "Iksha", "Ikshana", "Ikshit", "Ikshitha", "Ikshu", "Ikshula", "Ikshumalvi", "Ikshumiti", "Ina", "Inani", "Inayat", "Inayata", "Inbavalli", "Indira", "Indira Mohini", "Indrakshi", "Indramohini", "Indrani", "Indrapriya", "Indrasena", "Indratha", "Indrayani",
            "Indu", "Indubala", "Induja", "Indukaksha", "Indukala", "Indukalika", "Indukamala", "Kamini", "Jarul", "Jasmin", "Jasmit", "Jasu", "Jasum", "Jasweer", "Jayati", "Jayavanti", "Jaya", "Jayita", "Jayitri", "Jayna", "Jayne", "Jayshri", "Jeevanlata", "Jeevika", "Jeevitha", "Jenya", "Jetashri", "Jhalak", "Jharna", "Jhilmil", "Jhinuk",
            "Jigisha", "Jigna", "Jignasa", "Jigya", "Jigyasa", "Kanchi", "Kandhara", "Kangana", "Kani", "Kanika", "Kanimoli", "Kanira", "Kanishka", "Kanitha", "Kanjari", "Kankalini", "Kavita", "Kaviya", "Kavni", "Kavya", "Kayalvili", "Keemaya", "Keertana", "Keerthi", "Kenga", "Kenisha", "Kerani", "Kesar", "Kesari", "Keshi", "Keshika",
            "Keshini", "Ketaki", "Ketana", "Kumari", "Kumkum", "Madan Mohini", "Madan Rekha", "Madana", "Madanika", "Madhavi", "Madhavilata", "Madhija", "Madhooki", "Madhu", "Madhu Chandi", "Madhu Nisha", "Madhubala", "Madhuchanda", "Madhugeeta", "Madhuilka", "Madhuja", "Madhuksara", "Madhula", "Madhulata", "Madhulekha", "Madhulika", "Madhumadhvi",
            "Madhumalati", "Nagina", "Naima", "Naina", "Naiya", "Najma", "Nalini", "Namita", "Namrata", "Namya", "Nanda", "Nandana", "Nandika", "Nandini", "Nandita", "Nangai", "Narayani", "Mukunda", "Mukundi", "Mullai", "Mullaivadivu", "Mundri", "Munia", "Muniya", "Munni", "Murad", "Murali", "Mutyam", "Niharika", "Nikhila", "Nikhita", "Nila",
            "Nilasha", "Nilavoli", "Nilaya", "Nileen", "Nilima", "Nimisha", "Nina", "Nipa", "Niraimadhi", "Niral", "Niranjana", "Oindrila", "Oja", "Ojal", "Ojasvi", "Ojaswini", "Oma", "Omaja", "Omala", "Omana", "Omisha", "Omkareshwari", "Omkari", "Omvati", "Parajika", "Parama", "Parameshwari", "Paramita", "Paravi", "Parbarti", "Pari", "Paridhi",
            "Parina", "Parinita", "Parivita", "Pariyat", "Parmita", "Parnal", "Parnashri", "Parnavi", "Parni ", "Parnik", "Parnika", "Parnita", "Pooja", "Poonam", "Poorbi", "Poorna", "Poornakamala", "Poornima", "Poorva", "Poorvaganga", "Poorvaja", "Poorvi", "Poushali", "Priyala", "Priyam", "Priyamvada", "Priyanka", "Priyanvada", "Priyasha",
            "Prutha", "Preet", "Preetha", "Preeti", "Prem", "Prema", "Premala", "Premila", "Prerana", "Prerna", "Preshti", "Rishita", "Rishma", "Rishmitha", "Rita", "Rithika", "Riti", "Ritika", "Ritisha", "Ritsika", "Ritu", "Rituparna", "Riya", "Rekha", "Reneeka", "Renu", "Renuka", "Resham", "Reshika", "Reshma", "Reshmi", "Reva", "Revathi",
            "Revati", "Rucha", "Ruchi", "Ruchika", "Ruchita", "Ruchitha", "Rudra", "Rudrabhiravi", "Rudrakali", "Rudrani", "Sajala", "Sajani", "Sajili", "Sajni", "Saketha", "Sakhi", "Sakina", "Sakshi", "Salena", "Salila", "Salima", "Salma", "Saloni", "Samanmitha", "Santosh", "Santoshi", "Santushti", "Sanvali", "Sanvi", "Sanwari", "Sanyakta",
            "Sanyogita", "Sanyukta", "Sonam", "Soneera", "Sonia", "Sonika", "Suhasini", "Suhavi", "Suhela", "Suhina", "Suhitha", "Suhrita", "Sujala", "Sujata", "Sujaya", "Sujitha", "Sukaksha", "Sukanya", "Sukeshi", "Sukhada", "Suryadita", "Suryakanti", "Suryani", "Sushama", "Sushanti", "Sushila", "Sushma", "Sushmita", "Sushobhana", "Susila",
            "Susita", "Susmita", "Susumna", "Tanuja", "Tanusha", "Tanulata", "Tanushri", "Tanvee", "Tanya", "Tapani", "Tapasi", "Tapasya", "Tapati", "Tara", "Tarangini", "Tarika", "Tarini", "Taruni", "Unnati", "Upadesi", "Upasana", "Urmika", "Urmila", "Urvasi",
            "Aadit", "Aaditeya", "Aaditya", "Aadya", "Aafreen", "Aagam", "Aagneya", "Aahlaad", "Achalendra", "Achalesvara", "Achalraj", "Achanda", "Acharya", "Acharyasuta", "Achindra", "Agrim", "Agriya", "Agyeya", "Ahan", "Ahijit", "Ahilan", "Ahmad", "Ahsan", "Aijaz", "Aiman", "Ainesh", "Airaawat", "Aja", "Ajaat", "Ajaatshatru", "Ajamil", "Ajanta",
            "Ajatashatru", "Ajay", "Ajeet", "Ajendra", "Ajinkya", "Ajisth", "Ajit", "Ajitaabh", "Ajitabh", "Ajitesh", "Ajmal", "Anjor", "Anjum", "Anjuman", "Ankal", "Ankit", "Ankur", "Ankush", "Elephants", "Anmol", "Annuabhuj", "Anoop", "Anram", "Ansh", "Anshu", "Anshuk", "Anshul", "Anshumaan", "Anshumat", "Antara", "Antariksh", "Antim", "Anu",
            "Anubhav", "Banbihari", "Bandhu", "Bandhul", "Bankebihari", "Bankim", "Bankimchandra", "Bansilal", "Barid", "Baridbaran", "Barindra", "Barun", "Basanta", "Basavaraj", "Bashir", "Basistha", "Basudha", "Bikram", "Bilva", "Bimal", "Bindusar", "Birbal", "Bitasok", "Bodhan", "Boudhayan", "Brahma", "Brahmaji", "Brahmabrata", "Brahmadutt",
            "Chanakya", "Chanchal", "Chanchareek", "Chandak", "Chandan", "Chandavarman", "Chandeedaas", "Chander", "Chandra", "Chandraabhaa", "Chandraaditya", "Chandraanan", "Chandraayan", "Chandrabhaga", "Chandrabhan", "Chandrachur", "Chandragupt", "Chandrahaas", "Chithayu", "Chitrabaahu", "Chitrabhanu", "Chitragupt", "Chitragupta", "Chitraketu",
            "Chitraksh", "Chitral", "Chitrarath", "Chitrasen", "Chitresh", "Chitta", "Chittaprasad", "Chittaranjan", "Chittaswarup", "Chittesh", "Cholan", "Chudamani", "Chulbul", "Chunmay", "Deep", "Deepan", "Deepankar", "Deependra", "Deependu", "Deepesh", "Deepit", "Deeptanshu", "Deeptendu", "Deeptiman", "Deeptimoy", "Dehabhuj", "Dev", "Dev Kumar",
            "Devaapi", "Devabrata", "Devansh", "Devaraj", "Devarpana", "Devarshi", "Devarya ", "Devashish", "Devbrata", "Devdarsh", "Devdas", "Devdatta", "Devdutta", "Deveedaas", "Devendra", "Devendranath", "Devesh", "Deveshwar", "Devguru", "Devi", "Devidas", "Ekaant", "Ekaatmaa", "Ekachakra", "Ekachandra", "Ekachith", "Ekadant", "Ekagrah", "Ekaksha",
            "Ekalavya", "Iridari", "Giridhar", "Giridhari", "Girijanandan", "Girijapati", "Girik", "Girik", "Girilal", "Girindra", "Giriraaj", "Giriraj", "Girish", "Girivar", "Girja", "Girvaan", "Gul", "Gulab", "Gulabrai", "Gulal", "Gulam", "Gulfam", "Gulsan", "Gulshan", "Gulwant", "Gulzarilal", "Gumwant", "Guna", "Gunin", "Gunina", "Gunjan", "Gunkar",
            "Gunpreet", "Guntas", "Gunvant", "Gunwant", "Gupil", "Gurbachan", "Gurbakhsh", "Gurcharan", "Gurchet", "Gurdas", "Gurdaya", "Gurdayal", "Gurdayal", "Hars", "Harsha", "Harshad", "Harshal", "Harshaman", "Harshavardhan", "Harshil", "Harshini", "Harshit", "Harshita", "Harshul", "Harshvardhan", "Harteij", "Hasan", "Hashmat", "Hasit", "Hasmukh",
            "Hastin", "Havish", "Himaanshu", "Himachal", "Himaghna", "Himanish", "Himank", "Himanshu", "Himmat", "Himnish", "Hind", "Hindola", "Hiran", "Hiranmaya", "Hiranya", "Hiranyak", "Hiren", "Hirendra", "Hiresh", "Hitakrit", "Hitendra", "Isaivalan", "Isar", "Ish", "Ishaan", "Ishana", "Ishat", "Ishayu", "Ishir ", "Ishit", "Ishrat", "Ishwaas",
            "Ishwar", "Istardh", "Ithavari", "Jaidev", "Jaigath", "Jaigopal", "Jaikrishna", "Jaiman", "Jaimini", "Jainarayan", "Jaipal", "Jaipal", "Jairaj", "Jairam", "Jaisal", "Jaishankar", "Jaisukh", "Jaisukhg", "Jaithra", "Jaitra", "Jaival", "Jaivant", "Jaivardhan", "Jaiveer", "Jaiwant", "Jakarious", "Jeeva", "Jeeval", "Jeevan", "Jeevanprakash",
            "Jeevaraaj", "Jeevesh", "Jehangir", "Jenya", "Jhoomer", "Jhulier", "Jhumar", "Jigar", "Jignesh", "Kaarikaa", "Kaartikeya", "Kaashinaath", "Kabir", "Kadamb", "Kaditula", "Kailas", "Kailash", "Kailashchandra", "Kailashnath", "Kairav", "Kaivalya", "Kaladhar", "Kalanath", "Kalanidhi", "Kalap", "Kalapriya", "Kalash", "Kaleecharan", "Kalhans",
            "Kalicharan ", "Kalimohan", "Ketan", "Ketu", "Ketubh", "Keva", "Keval", "Kevalkishore", "Kevalkumar", "Keyur", "Khagendra", "Khagesh", "Khairiya", "Khajit", "Khalid", "Kharanshu", "Khazana", "Khemchand", "Khemprakash", "Khushal", "Khushwant", "Lakshya", "Lalam", "Lalan", "Lalchand", "Lalit", "Lalitaditya", "Lalitchandra", "Lalitesh",
            "Lalitkishore", "Lalitkumar", "Lalitlochan", "Lalitmohan", "Lambodar", "Madhuban", "Madhuchanda", "Madhuk", "Madhukant", "Madhukanta", "Madhukar", "Madhumay", "Madhup", "Madhur", "Madhusoodan", "Madhusudan", "Maharanth", "Maharath", "Maharshi", "Maharth", "Mahasvin", "Mahatru", "Mahaveer", "Mahavir", "Maheepati", "Mahendra", "Mahesh",
            "Maheshwar", "Mahipati", "Mahir", "Mahish", "Mahith", "Mahtab", "Mainak", "Mairava", "Maitreya", "Makarand", "Makhesh", "Makrand", "Makul", "Makur", "Malak", "Malank", "Malay", "Namasyu", "Nambi", "Namdev", "Namish", "Namit", "Nanak", "Nand", "Nanda", "Nandak", "Nandakishor", "Nandakumar", "Nandan", "Nandi", "Nandin", "Nandish", "Nand-Kishore",
            "Nandlaal", "Nand-Nandan", "Naotau", "Narad", "Narahari", "Narun", "Naruna", "Natesh", "Nateshwar", "Nathan", "Nathin", "Natraj", "Natwar", "Naubahar", "Nauhar", "Nauka", "Navaj", "Naval", "Navalan", "Navaneet", "Navashen", "Naveen", "Navendu", "Navin", "Navinchandra", "Navnit", "Navrang", "Nishikar", "Nishil", "Nishipal", "Nishipat", "Nishit",
            "Nishita", "Nishith", "Nishkama", "Nishkarsh", "Nishok", "Nissim", "Nisyaanthan", "Niteesh", "Nitesh", "Nithik", "Omanand", "Omar", "Omarjeet", "Omesa ", "Omeshwar", "Omja", "Omkar", "Omkarnath", "Ompati", "Omprakash", "Omrao", "Pandita", "Panduranga", "Pandya", "Panini", "Pankaj", "Pankajalochana", "Pankajan", "Pankajeet", "Panmoli", "Panna",
            "Praket", "Prakhar", "Prakrit", "Prakriti", "Prakul", "Pralay", "Pramath", "Pramesh", "Pramit", "Pramod", "Pramodan", "Pramsu", "Pramukh", "Ajesh", "Rajinipati", "Rajit", "Rajiv", "Rajivlochan", "Rajivnayan", "Rajkumar", "Rajrishi", "Raju", "Rajyeshwar", "Rakesh", "Rakshan", "Raktakamal", "Ram", "Rishik", "Rishikesh", "Rishit", "Ritesh", "Rithik",
            "Riti", "Rituraaj", "Rituraj", "Ritvik", "Rochan", "Rohak", "Rohan", "Rohanlal", "Rohin", "Adaiappan", "Sadanand", "Sadar", "Sadashiv", "Sadavir", "Sadeepan", "Sadhan",

    };


}
