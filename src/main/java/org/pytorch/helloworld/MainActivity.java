package org.pytorch.helloworld;
import android.content.Context;
import android.os.Build;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {

  public static final String TAG = MainActivity.class.getSimpleName();
  public static final String mPath = "contact.txt";
  public static final String mPathTemplate = "template.txt";
  private static final List<String> agree = Arrays.asList("oke", "ok");
  private static final List<String> edit = Arrays.asList("sua", "s");
  private static final List<String> cancle = Arrays.asList("k", "khong");
  private static final List<String> startCommand = Arrays.asList("Tin nhắn đã được gửi", "Bạn muốn tôi giúp gì?","Bạn muốn nhắn tin gì?","Tôi không hiểu bạn nói gì, làm ơn có thể nói lại được không?","Hủy gửi tin nhắn");
  private List<String> mPhoneBook;
  private ContactFileReader mReadContact;
  private List<String> listTemplate;
  private TemplateFileReader mReadTemplate;
  private RecyclerView rcvMessage;
  private MessageAdapter messageAdapter;
  private List<Message> mListMessage;
  private String contact;
  private String message;
  private String vinfastMessage;
//  private int useCase = 0;
  int cls=0;

  private EditText editMessage;
  private Button btnSend;

  @RequiresApi(api = Build.VERSION_CODES.O)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //load cls model
    Module module_cls = null;
    try {
      module_cls = LiteModuleLoader.load(assetFilePath(getApplicationContext(), "cls_best_mobile.ptl"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // load ner model
    Module module = null;
    try {
      module = LiteModuleLoader.load(assetFilePath(getApplicationContext(), "ner_best_mobile.ptl"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    Log.i("PytorchNER", "Load model successful" + module.toString());
//    String textInput = "nhắn tin nhắn bảo vợ ăn ơi hôm nay anh về muộn nhé gửi đến cho vợ yêu";
//    String textInput = "gửi tin nhắn mai có được nghỉ không mai cho mai bờ nờ tờ";
//    String textInput = "gửi tin cho anh hùng modec rằng hôm sau lên ký hợp đồng làm ăn nhé anh";
//    String textInput = "nhắn tin cho số 0123995234 bảo là hôm nay anh về muộn nhé";
//    String textInput = "soạn tin có nội dung là đi chơi ko em cho hot girl khóa dưới";
//    String textInput = "soạn đi chơi ko em cho Nam khóa dưới";
//    String textInput = "soạn cho nam khóa trên là đi chơi ko em";
//    String textInput = "gửi tin nhắn bảo nam khóa dưới là đi chơi ko em";
//    String textInput = "gửi thư với nội dung đi chơi ko em rồi gửi cho nam";
//    String textInput = "soạn tin có nội dung là đi chơi ko em cho em hot girl khóa dưới";
//    String textInput = "gửi luôn và ngay tin nhắn cho nhà cứu trợ báo gửi tiền cho anh phát vợ ơi anh hết con mẹ nó tiền tiêu rồi xe đang hỏng";
//    String textInput = "nhắn tin cho anh năm giường gỗ gửi giường chưa anh ơi";
//    String textInput = "gửi cho mama hôm nay con không về nhà đâu";
//    String textInput = "gửi cho mama tin nhắn hôm nay con không về nhà đâu";
//    String textInput = "hey Vinfast báo vợ dùm tối nay tui không về";
//    String textInput = "Nhắn cho anh Đông";

    // Read contact
    mReadContact = new ContactFileReader(this);
    mPhoneBook = mReadContact.readLine(mPath);
    for (String string : mPhoneBook)
    Log.d(TAG, string);
    Log.i("PytorchNER", "phonebook" + mPhoneBook.toString());
//  read template
    mReadTemplate = new TemplateFileReader(this);
    listTemplate = mReadTemplate.readLine(mPathTemplate);
    for (String string : listTemplate)
    Log.i("PytorchNER", "list template" + listTemplate.toString());


    // showing className on UI
//    TextView textView = findViewById(R.id.textContact);
//    textView.setText(predict_contact_);
//    TextView textView1 = findViewById(R.id.textMessage);
//    textView1.setText(predict_message_);


    editMessage = findViewById(R.id.edt_message);
    btnSend = findViewById(R.id.btn_send);
    rcvMessage = findViewById(R.id.rcv_message);

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    rcvMessage.setLayoutManager(linearLayoutManager);

    mListMessage = new ArrayList<>();
    messageAdapter = new MessageAdapter();
    messageAdapter.setData(mListMessage);

    rcvMessage.setAdapter(messageAdapter);



    vinfastMessage = "Bạn muốn tôi giúp gì?";
    mListMessage.add(new Message(vinfastMessage));

    Module finalModule = module;
    Module finalModule_cls = module_cls;
    btnSend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

//        textInput = sendMessage();

        vinfastMessage = mListMessage.get(mListMessage.size()-1).getMessage();
//        Log.i("post", "vinfastMessage " + vinfastMessage);
//        Toast.makeText(MainActivity.this, vinfastMessage, Toast.LENGTH_SHORT).show();


        String UserMessage = editMessage.getText().toString().trim();
        if (TextUtils.isEmpty(UserMessage)){
          Toast.makeText(MainActivity.this, "empty", Toast.LENGTH_SHORT).show();
        }
        mListMessage.add(new Message(UserMessage));

        if (startCommand.contains(vinfastMessage)){
          Log.i("post", "check\t a");
          if (listTemplate.contains(UserMessage)) {
            String botMessage = "Bạn muốn nhắn tin gì?";
            mListMessage.add(new Message(botMessage));
          }
          else {
            cls = predict_cls(UserMessage, finalModule, finalModule_cls);
            Log.i("post", "message\t" + UserMessage);
            Log.i("post", "cls\t" + cls);
            String [] result;
            result = predict_message(UserMessage, finalModule);
            contact = result[0];
            message = result[1];
            if (cls==1){
              String botMessage = "Bạn muốn nhắn" +" cho "+contact+" với nội dung là "+ message +", phải không?";
              mListMessage.add(new Message(botMessage));
            }
            else if (cls==2){
              String botMessage = "Bạn muốn nhắn tới ai?";
              mListMessage.add(new Message(botMessage));
            }
            else if (cls==3){
//              message = predict_message(UserMessage, finalModule);
              String botMessage = "Bạn muốn nhắn với nội dung gì?";
              mListMessage.add(new Message(botMessage));
            }
            else{
              String botMessage = "Tôi không hiểu bạn nói gì, làm ơn có thể nói lại được không?";
              mListMessage.add(new Message(botMessage));
            }
          }
        }
        else if(cls==2){
          String [] result;
          result = predict_message(UserMessage, finalModule);
          contact = result[0];
//          message = result[1];
          String botMessage = "Bạn muốn nhắn" +" cho "+contact+" với nội dung là "+ message +", phải không?";
          mListMessage.add(new Message(botMessage));
          cls = 0;
        }
        else if(cls==3){
          String [] result;
          result = predict_message(UserMessage, finalModule);
          message = result[1];
          String botMessage = "Bạn muốn nhắn" +" cho "+contact+" với nội dung là "+ message +", phải không?";
          mListMessage.add(new Message(botMessage));
          cls = 0;
        }
        else {
          Log.i("post", "check\t b");
          if (agree.contains(UserMessage)){
            String botMessage = "Tin nhắn đã được gửi";
            mListMessage.add(new Message(botMessage));
          }
          else if (edit.contains(UserMessage)){
            String botMessage = "Bạn muốn nhắn tin gì?";
            mListMessage.add(new Message(botMessage));
          }
          else if (cancle.contains(UserMessage)){
            String botMessage = "Hủy gửi tin nhắn";
            mListMessage.add(new Message(botMessage));
          }
          else {
            String botMessage = "Tôi không hiểu bạn nói gì, làm ơn có thể nói lại được không?";
            mListMessage.add(new Message(botMessage));
//            String botMessage_ = "Bạn muốn nhắn tin gì?";
//            mListMessage.add(new Message(botMessage_));
          }
        }

        messageAdapter.notifyDataSetChanged();
        rcvMessage.scrollToPosition(mListMessage.size()-1);

//        Log.i("post", "test\t"+messageAdapter.getItemCount());
        editMessage.setText("");
      }
    });



//    Log.i("post", "predict trigger\t"+mListMessage);
  }

  private int predict_cls(String textInput, Module finalModule,Module finalModule_cls){
    long[] textIds = new long[textInput.length()];
    char[] textInputChar = textInput.toCharArray();
    for (int i = 0; i < textIds.length; i++) {
      textIds[i] = (long) textInputChar[i];
    }
    Tensor tensorString = Tensor.fromBlob(textIds, new long[]{1, textIds.length});
    long[] stringIds = finalModule.runMethod("encode_input", IValue.from(tensorString)).toLongList();
//    Log.i("PytorchNER", "Input ids: " + Arrays.toString(stringIds));
    Tensor tensor = Tensor.fromBlob(stringIds, new long[]{1, stringIds.length});
    // CLS
//    final Tensor outputTensor = finalModule_cls.forward(IValue.from(tensor)).toTensor();
    final Tensor outputTensor = finalModule_cls.runMethod("forward", IValue.from(tensor)).toTensor();
    // getting tensor content as java array of floats
    final float[] scores = outputTensor.getDataAsFloatArray();
    // searching for the index with maximum score
    float maxScore = -Float.MAX_VALUE;
    int maxScoreIdx = -1;
    for (int i = 0; i < scores.length; i++) {
      if (scores[i] > maxScore) {
        maxScore = scores[i];
        maxScoreIdx = i;
      }
    }

    Log.i("post", "scores\t"+scores.toString());
    Log.i("post", "max scores\t"+maxScore);

    return maxScoreIdx;
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  private String[] predict_message(String textInput, Module finalModule){
        long[] textIds = new long[textInput.length()];
        char[] textInputChar = textInput.toCharArray();
        for (int i = 0; i < textIds.length; i++) {
          textIds[i] = (long) textInputChar[i];
        }
        Tensor tensorString = Tensor.fromBlob(textIds, new long[]{1, textIds.length});
        long[] stringIds = finalModule.runMethod("encode_input", IValue.from(tensorString)).toLongList();
        Log.i("PytorchNER", "Input ids: " + Arrays.toString(stringIds));
        Tensor tensor = Tensor.fromBlob(stringIds, new long[]{1, stringIds.length});
//        // CLS
////    final Tensor outputTensor = module_cls.forward(IValue.from(tensor)).toTensor();
//        final Tensor outputTensor = finalModule_cls.runMethod("forward", IValue.from(tensor)).toTensor();
//        // getting tensor content as java array of floats
//        final float[] scores = outputTensor.getDataAsFloatArray();
//        // searching for the index with maximum score
//        float maxScore = -Float.MAX_VALUE;
//        int maxScoreIdx = -1;
//        for (int i = 0; i < scores.length; i++) {
//          if (scores[i] > maxScore) {
//            maxScore = scores[i];
//            maxScoreIdx = i;
//          }
//        }
        // NER
        IValue[] result = finalModule.runMethod("decode", IValue.from(tensor)).toList()[0].toList();
        String[] labelResult = new String[result.length];
        for (int i = 0; i < labelResult.length; i++) {
          labelResult[i] = result[i].toStr();
        }

//        Log.i("PytorchNER", "Result: " + Arrays.toString(labelResult));
//        Log.i("post", "Result_cls: " + Arrays.toString(scores));
//        Log.i("post", "Result_cls: " + maxScoreIdx);
        String predict_trigger = "";
        String predict_contact = "";
        String predict_message = "";
        List<String> text_split = new ArrayList<String>(Arrays.asList(textInput.split(" ")));
//    System.out.println(text_split);
        // trigger word check
        Log.i("post", "label\t" + Arrays.toString(labelResult));
        Log.i("post", "sentence\t" + text_split);
        boolean check_btg = check(labelResult, "B-TG");
        boolean check_itg = check(labelResult, "I-TG");

        int start_CN = 0;
        int start_MC = 0;
        int max_len = text_split.size();
        // Trigger
        if (check_btg == TRUE && check_itg == TRUE) {
          int begin = Arrays.asList(labelResult).indexOf("B-TG");
          int span = countElement(Arrays.asList(labelResult), "I-TG");
          int end = begin + span;
          List<String> trigger_list = text_split.subList(begin, end);
          String joined = String.join(" ", trigger_list);
          predict_trigger += joined;
        }
        else if (check_btg == TRUE && check_itg == FALSE) {
          int begin = Arrays.asList(labelResult).indexOf("B-TG");
          int span = 1;
          int end = begin + span;
          List<String> trigger_list = text_split.subList(begin, end);
          String joined = String.join(" ", trigger_list);
          predict_trigger += joined;

        }
        else if (check_btg == FALSE && check_itg == TRUE) {
          int begin = Arrays.asList(labelResult).indexOf("I-TG");
          int span = countElement(Arrays.asList(labelResult), "I-TG");
          int end = begin + span;
          List<String> trigger_list = text_split.subList(begin, end);
          String joined = String.join(" ", trigger_list);
          predict_trigger += joined;
        }

        // Contact check
        boolean check_bcn = check(labelResult, "B-CN");
        boolean check_icn = check(labelResult, "I-CN");
        if (check_bcn == TRUE && check_icn == TRUE) {
          List<Integer> indiceList;
          indiceList = collectIndices(Arrays.asList(labelResult), "B-CN");
          if (indiceList.size() == 1) {
            int begin = Arrays.asList(labelResult).indexOf("B-CN");
            start_CN = begin;
//                int span = countElement(Arrays.asList(labelResult), "I-CN");
            int end = begin + 1;
            while (end < max_len && Arrays.asList(labelResult).get(end).equals("I-CN")) {
              end+=1;
            }

            List<String> contact_list = text_split.subList(begin, end);
            String joined = String.join(" ", contact_list);
            predict_contact += joined;
          } else {
            int idx_contact = 0;
            for (int i=0; i < indiceList.size();i++){
              int indice = indiceList.get(i);
              if (indice+1 < max_len){
                if (Arrays.asList(labelResult).get(indice+1).equals("I-CN")){
                  idx_contact = indice;
                  break;
                }
              }
            }
            int begin = idx_contact;
            start_CN = begin;
            int end = begin + 1;
            while (end < max_len && Arrays.asList(labelResult).get(end).equals("I-CN")){
              end+=1;
            }
            List<String> trigger_list = text_split.subList(begin, end);
            String joined = String.join(" ", trigger_list);
            predict_contact += joined;
          }
        } else if (check_bcn == TRUE && check_icn != TRUE) {
          List<Integer> indiceList;
          indiceList = collectIndices(Arrays.asList(labelResult), "B-CN");
          if (indiceList.size() == 1) {
            int begin = Arrays.asList(labelResult).indexOf("B-CN");
            start_CN = begin;
            int span = countElement(Arrays.asList(labelResult), "B-CN");
            int end = begin + span;
            List<String> trigger_list = text_split.subList(begin, end);
            String joined = String.join(" ", trigger_list);
            predict_contact += joined;
          } else {
            int begin = indiceList.get(0);
            start_CN = begin;
//                int span = countElement(Arrays.asList(labelResult), "B-CN");
            int span = 1;
            int end = begin + span;
            List<String> trigger_list = text_split.subList(begin, end);
            String joined = String.join(" ", trigger_list);
            predict_contact += joined;
          }
        } else if (check_bcn != TRUE && check_icn == TRUE) {
          int begin = Arrays.asList(labelResult).indexOf("I-CN");
          start_CN = begin;
          int span = countElement(Arrays.asList(labelResult), "I-CN");
          int end = begin + span;
          List<String> trigger_list = text_split.subList(begin, end);
          String joined = String.join(" ", trigger_list);
          predict_contact += joined;
        } else {
          predict_contact = "";
        }
        // Message content check
        boolean check_bmc = check(labelResult, "B-MC");
        boolean check_imc = check(labelResult, "I-MC");
        if (check_bmc == TRUE && check_imc == TRUE) {
          List<Integer> indiceList;
          indiceList = collectIndices(Arrays.asList(labelResult), "B-MC");
          if (indiceList.size() == 1) {
            int begin = Arrays.asList(labelResult).indexOf("B-MC");
            start_MC = begin;
            int span = countElement(Arrays.asList(labelResult), "I-MC");
            int end = begin + span + 1;
            if (end < max_len && start_CN < begin){
              end = max_len;
            }

            if (end == max_len-1 && text_split.get(max_len-1).equals("B-TG")){
              end +=1;
            }
            List<String> trigger_list = text_split.subList(begin, end);
            String joined = String.join(" ", trigger_list);
            predict_message += joined;
          }
          else {
            int[] MC = counter_MC(Arrays.asList(labelResult), "B-MC", "I-MC");
            int begin = indiceList.get(0);
            start_MC = begin;
            int span = MC[2];
            int end = begin + span + 1;
            if (start_CN < start_MC){
              end = max_len;
            }
            else if (start_CN > start_MC){
              int end_CN = max_len;
              predict_contact = "";
              List<String> contact = text_split.subList(start_CN, end_CN);
              String joined_contact = String.join(" ", contact);
              predict_contact += joined_contact;
            }
            else{
              begin = MC[0];
              span = MC[1];
              end = begin + span +1;
            }

            List<String> trigger_list = text_split.subList(begin, end);
            String joined = String.join(" ", trigger_list);
            predict_message += joined;
          }
        } else if (check_bmc == TRUE && check_imc != TRUE) {
          List<Integer> indiceList;
          indiceList = collectIndices(Arrays.asList(labelResult), "B-MC");
          if (indiceList.size() == 1) {
            int begin = Arrays.asList(labelResult).indexOf("B-MC");
            start_MC = begin;
//                int span = countElement(Arrays.asList(labelResult), "B-MC");
            int span = 1;
            int end = begin + span;
            List<String> trigger_list = text_split.subList(begin, end);
            String joined = String.join(" ", trigger_list);
            predict_message += joined;
          } else {
            int begin = indiceList.get(0);
            start_MC = begin;
//                int span = countElement(Arrays.asList(labelResult), "B-MC");
            int span = 1;
            int end = begin + span;
            List<String> trigger_list = text_split.subList(begin, end);
            String joined = String.join(" ", trigger_list);
            predict_message += joined;
          }
        } else if (check_bmc != TRUE && check_imc == TRUE) {
          int begin = Arrays.asList(labelResult).indexOf("I-MC");
          start_MC = begin;
          int span = countElement(Arrays.asList(labelResult), "I-MC");
          int end = begin + span;
          List<String> trigger_list = text_split.subList(begin, end);
          String joined = String.join(" ", trigger_list);
          predict_message += joined;
        } else {
          predict_message = "";
        }

        if (predict_contact.equals("")) {
          predict_contact = "empty";
        }
        else{
          predict_contact = correct_contact(predict_contact);
        }

        if (predict_message.equals("")) {
          predict_message = "empty";
        }

        predict_message = correct_message(predict_message);

        String predict_contact_ = "";
        String predict_message_ = "";

        // Rules-based regex
        if (predict_contact.equals("empty") || predict_message.equals("empty")) {
          predict_contact_ = extract_contact_regex(textInput, predict_contact);
          predict_message_ = extract_message_regex(textInput, predict_message);
          // Rule-based regex 2
          if (predict_contact_.equals("empty") || predict_message_.equals("empty")) {
            predict_contact_ = extract_contact_regex_2(textInput, predict_contact);
            predict_message_ = extract_message_regex_2(textInput, predict_message);
          }
          else {
            // Contact matching
            // if not using the option, comment that
            String[] results;
            results = contact_matching(textInput, predict_contact_, predict_message_, mPhoneBook);
            predict_contact_ = results[0];
            predict_message_ = results[1];
          }
        }
        else {
          // Contact matching
          // if not using the option, comment that
          String[] results;
          results = contact_matching(textInput, predict_contact, predict_message, mPhoneBook);
          predict_contact_ = results[0];
          predict_message_ = results[1];
        }

        Log.i("post", "predict contact\t"+predict_contact_);
        Log.i("post", "predict message\t"+predict_message_);
        Log.i("post", "predict trigger\t"+predict_trigger);

        Toast.makeText(MainActivity.this, textInput, Toast.LENGTH_SHORT).show();
    String[] results = new String[2];
    results[0] = predict_contact_;
    results[1] = predict_message_;

    return results;
//    return predict_contact;
  }

//  private String sendMessage() {
//    String strMessage = editMessage.getText().toString().trim();
//    if (TextUtils.isEmpty(strMessage)){
//      Toast.makeText(MainActivity.this, "empty", Toast.LENGTH_SHORT).show();
//    }
//
//    mListMessage.add(new Message(strMessage));
//    messageAdapter.notifyDataSetChanged();
//    rcvMessage.scrollToPosition(mListMessage.size()-1);
//
//
//    editMessage.setText("");

//    Log.i("post", "message\t"+mListMessage.get(mListMessage.size()-1).getMessage());

//    return mListMessage.get(mListMessage.size()-1).getMessage();
//  }

  private static boolean check(String[] arr, String toCheckValue) {
    // check if the specified element
    // is present in the array or not
    // using contains() method
    boolean test
            = Arrays.asList(arr)
            .contains(toCheckValue);
    // Print the result
    System.out.println("Is " + toCheckValue
            + " present in the array: " + test);
    return test;
  }
  private static int countElement(List<String> arr, String toCheckValue) {
    int occurrences = 0;
    for (int i = 0; i < arr.size(); i++) {
      if (arr.get(i).equals(toCheckValue)) {
        occurrences += 1;
      }
    }
    return occurrences;
  }
  private static List<Integer> collectIndices(List<String> arr, String toCheckValue) {
    List<Integer> indiceList = new ArrayList<>();
    for (int i = 0; i < arr.size(); i++) {
      if (arr.get(i).equals(toCheckValue)) {
        indiceList.add(i);
      }
    }
    return indiceList;
  }

  private static int[] counter_MC(List<String> labelResult, String startValue, String endValue){
    int max_len = labelResult.size();
    List<Integer> indiceList;
    indiceList = collectIndices(labelResult, startValue);
    int max_indice = 0;
    int idx_max_indice = 0;
    int counter_0 = 0;
    for(int i=0; i < indiceList.size(); i++){
      int j = indiceList.get(i) + 1;
      int counter = 0;
      while (j<max_len && labelResult.get(j).equals(endValue)){
        counter ++;
        j ++;
      }
      if (i==0){
        counter_0 = counter;
      }
      if (counter >= max_indice){
        idx_max_indice = i;
        max_indice = counter;
      }
    }

    int[] result = new int[3];
    result[0] = idx_max_indice; // idx
    result[1] = max_indice;// length
    result[2] = counter_0; // lenght_0
    return result;
  }
  public static String assetFilePath(Context context, String assetName) throws IOException {
    File file = new File(context.getFilesDir(), assetName);
    if (file.exists() && file.length() > 0) {
      return file.getAbsolutePath();
    }
    try (InputStream is = context.getAssets().open(assetName)) {
      try (OutputStream os = new FileOutputStream(file)) {
        byte[] buffer = new byte[4 * 1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
          os.write(buffer, 0, read);
        }
        os.flush();
      }
      return file.getAbsolutePath();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  public static String correct_contact(String contact) {
    List<String> text_split = new ArrayList<String>(Arrays.asList(contact.split(" ")));
    for(int i=0; i < text_split.size(); i++){
      if (text_split.get(i).equals("cho")) {
        text_split.remove(i);
      }
    }
    String joined = String.join(" ", text_split);
    String predict_contact = "";
    predict_contact += joined;

    return predict_contact;
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  public static String correct_message(String message) {
    List<String> text_split = new ArrayList<String>(Arrays.asList(message.split(" ")));
    if (text_split.get(0).equals("là")) {
      text_split.remove(0);
    }
    String joined = String.join(" ", text_split);
    String predict_message = "";
    predict_message += joined;
    return predict_message;
  }

  public static String extract_contact_regex(String text, String contact) {
    Pattern pattern = Pattern.compile("(?:soạn tin nhắn hỏi lại|gửi tin nhắn hỏi lại|soạn tin nhắn cho|gửi tin nhắn cho|gửi tin nhắn bảo|soạn tin nhắn tới|soạn tin nhắn bảo|gửi tin nhắn hỏi|gửi tin sms cho|soạn tin nhắn hỏi|nhắn lại cho|gửi tin nhắn|gửi tin tới|nhắn bảo với|gửi thư|đáp lại|trả lời|phản hồi|nhắn cho|nhắn hỏi|bảo với|soạn cho|nhắn lại|gửi lại|gửi cho|đáp|reply|bảo|báo|gửi|gởi|nhắn|kêu)\\s(.+?)\\s(?:với nội dung tin nhắn là|với nội dung tin nhắn|với nội dung là|với nội dung|một tin là|tin nhắn là|nội dung là|tin nhắn|hỏi xem|hỏi là|hỏi rằng|bảo rằng|bảo là|nói là|là|rằng|bảo|hỏi)(?!\\S)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(text);

    while(matcher.find()) {
      contact = matcher.group(1);
    }
    return contact;
  }

  public static String extract_message_regex(String text, String message) {
    Pattern pattern = Pattern.compile("(?:soạn tin nhắn hỏi lại|gửi tin nhắn hỏi lại|soạn tin nhắn cho|gửi tin nhắn cho|gửi tin nhắn bảo|soạn tin nhắn tới|soạn tin nhắn bảo|gửi tin nhắn hỏi|gửi tin sms cho|soạn tin nhắn hỏi|nhắn lại cho|gửi tin nhắn|gửi tin tới|nhắn bảo với|gửi thư|đáp lại|trả lời|phản hồi|nhắn cho|nhắn hỏi|bảo với|soạn cho|nhắn lại|gửi lại|gửi cho|đáp|reply|bảo|báo|gửi|gởi|nhắn|kêu)\\s(.+?)\\s(?:với nội dung tin nhắn là|với nội dung tin nhắn|với nội dung là|với nội dung|một tin là|tin nhắn là|nội dung là|tin nhắn|hỏi xem|hỏi là|hỏi rằng|bảo rằng|bảo là|nói là|là|rằng|bảo|hỏi)(?!\\S)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(text);

    String not_message = "";
    while(matcher.find()) {
      not_message = matcher.group(0);
    }
    int begin = not_message.length();
    int end = text.length();

    message = text.substring(begin, end);
    return message.trim();
  }

  public static String extract_message_regex_2(String text, String message) {
    Pattern pattern = Pattern.compile("(?:gửi tin nhắn theo cú pháp|soạn tin nhắn có nội dung là|gửi tin nhắn có nội dung là|gửi thư có nội dung là|gửi thư với nội dung là|gửi thư với nội dung|soạn tin có nội dung là|gửi tin có nội dung là|gửi tin nhắn hỏi|gửi tin nhắn|nhắn lại cho|gửi thư|gửi sms|nhắn bảo|soạn tin|phản hồi|đáp lại|nhắn lại|trả lời|nhắn cho|nhắn hỏi|nhắn tin|gửi lại|gửi cho|gửi tin|đáp|reply|bảo|báo|gửi|gởi|nhắn|soạn)\\s(.+?)\\s(?:gửi đến số có tên trong danh bạ|cho người có trong danh bạ|rồi gửi cho|gửi đến số điện thoại|gửi tới số điện thoại|gửi cho số điện thoại|gửi nhanh đến|gửi gấp cho|gửi đến số|gửi cho số|gửi tới số|gửi gấp tới|gửi tới cho|gửi tới|gửi đến|cho|tới|đến)(?!\\S)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(text);

    while(matcher.find()) {
      // System.out.println(matcher.group(1));
//      Log.i("post", "check\t"+matcher.group(1));
      message = matcher.group(1);
      // not_message = text.substring(matcher.start(), matcher.end());
    }
    return message;
  }

  public static String extract_contact_regex_2(String text, String contact) {
    Pattern pattern = Pattern.compile("(?:gửi tin nhắn theo cú pháp|soạn tin nhắn có nội dung là|gửi tin nhắn có nội dung là|gửi thư có nội dung là|gửi thư với nội dung là|gửi thư với nội dung|soạn tin có nội dung là|gửi tin có nội dung là|gửi tin nhắn hỏi|gửi tin nhắn|nhắn lại cho|gửi thư|gửi sms|nhắn bảo|soạn tin|phản hồi|đáp lại|nhắn lại|trả lời|nhắn cho|nhắn hỏi|nhắn tin|gửi lại|gửi cho|gửi tin|đáp|reply|bảo|báo|gửi|gởi|nhắn|soạn)\\s(.+?)\\s(?:gửi đến số có tên trong danh bạ|cho người có trong danh bạ|rồi gửi cho|gửi đến số điện thoại|gửi tới số điện thoại|gửi cho số điện thoại|gửi nhanh đến|gửi gấp cho|gửi đến số|gửi cho số|gửi tới số|gửi gấp tới|gửi tới cho|gửi tới|gửi đến|cho|tới|đến)(?!\\S)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(text);

    String not_contact = "";
    while(matcher.find()) {
      // System.out.println(matcher.group(0));
      not_contact = matcher.group(0);
      // System.out.println(strTest.substring(matcher.start(), matcher.end()));
      // not_message = text.substring(matcher.start(), matcher.end());
    }
    int begin = not_contact.length();
    int end = text.length();

    contact = text.substring(begin, end);
    return contact.trim();
  }

  public static String[] regex_knowncontact(String text, String contact, String message, List<String> phonebook) {
    for(int i=0; i<phonebook.size(); i++){
      String contact_name = phonebook.get(i);
//      Log.i("post", "check\t"+contact_name);
      Pattern pattern = Pattern.compile("(?:soạn tin nhắn hỏi lại|gửi tin nhắn hỏi lại|soạn tin nhắn cho|gửi tin nhắn cho|gửi tin nhắn bảo|soạn tin nhắn tới|soạn tin nhắn bảo|gửi tin nhắn hỏi|gửi tin nhắn bảo|soạn tin nhắn hỏi|nhắn lại cho|gửi thư|đáp lại|trả lời|soạn tin|phản hồi|nhắn cho|nhắn hỏ|nhắn bảo|nhắn với|gửi cho|nhắn lại|gửi lại|đáp|reply|bảo|báo|gửi|gởi|nhắn|dặn)\\s"+contact_name+"(?!\\S)", Pattern.UNICODE_CASE);
      Matcher matcher = pattern.matcher(text);

      while(matcher.find()) {
        contact = contact_name;
      }
    }
//    Log.i("post", "a\t"+contact);
    message = message.replace(contact, "");
    message = message.trim();
    String[] results = new String[2];
    results[0] = contact;
    results[1] = message;

    return  results;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public static String[] contact_matching(String textInput, String pred_contact, String pred_message, List<String> phonebook){
    String contact = "";
    String message = "";
    boolean check_contact = check(phonebook.toArray(new String[phonebook.size()]), pred_contact);
    if (check_contact==TRUE){
      contact = pred_contact;
      message = pred_message;
    }
    else if (pred_contact.equals("empty") || pred_message.equals("empty")){
      String[] results = new String[2];
      results = regex_knowncontact(textInput, pred_contact, pred_message, phonebook);
      contact = results[0];
      message = results[1];
    }
    else {
      List<String> suggest_contact = new ArrayList<String>();
      suggest_contact = findUsingStream(pred_contact, phonebook);
      Log.i("post", "suggest contact\t"+suggest_contact);

      if (suggest_contact.size() > 0) {
        contact = suggest_contact.get(0);
      }
      else {
        List<String> CN_split = new ArrayList<String>(Arrays.asList(pred_contact.split(" ")));
        List<String> text_split = new ArrayList<String>(Arrays.asList(textInput.split(" ")));

        String[] words = {"anh", "chị"};
        boolean check_word_CN = check(words, CN_split.get(0));
        String seek_CN = "";
        int index = 0;
        if (check_word_CN == TRUE && CN_split.size() > 1){
          seek_CN = CN_split.get(1);
          index = 1;
        }
        else {
          seek_CN = CN_split.get(0);
          index = 0;
        }
        ArrayList<String> suggest_list = new ArrayList<String>();
        while (index < CN_split.size()-1){
          List<String> suggest_contact_2 = new ArrayList<String>();
          suggest_contact_2 = findUsingStream(pred_contact, phonebook);

          suggest_list.addAll(suggest_contact_2);
          seek_CN = seek_CN + " " + CN_split.get(index + 1);
          index++;
        }

        for(int i=0; i<suggest_list.size(); i++){
          boolean check_CN = textInput.contains(suggest_list.get(i).toString());
          if (check_CN == TRUE){
            contact = suggest_list.get(i);
            break;
          }
        }
      }
      if (contact == ""){ // contact not in phonebook
//        contact = "empty";
        contact = pred_contact;
        message = pred_message;
      }
      else {
        boolean contain = textInput.contains(contact);
        if (contain == true){
          String subtext = textInput.replace(contact, "<CN>");
          List<String> subtext_split = new ArrayList<String>(Arrays.asList(subtext.split(" ")));
          int idx_CN = subtext_split.indexOf("<CN>") + 1;
          List<String> trigger_list = subtext_split.subList(idx_CN, subtext_split.size());
          if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String joined = String.join(" ", trigger_list);
            message += joined;
          }
        }
        else{
          contact = pred_contact;
          message = pred_message;
        }
      }
    }
    String[] results = new String[2];
    results[0] = contact;
    results[1] = message;

    return results;
  }
  // Search contact in phonebook
  @RequiresApi(api = Build.VERSION_CODES.N)
  public static List<String> findUsingStream(String search, List<String> list) {
    List<String> matchingElements = list.stream()
            .filter(str -> str.trim().contains(search))
            .collect(Collectors.toList());
    return matchingElements;
  }
}