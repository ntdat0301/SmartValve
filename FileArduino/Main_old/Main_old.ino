/*
 * ------------Chương trình thực hiện điều khiển 3 VAN và gửi trạng thái các Van lên Server------------------------
 * ------------------------Có thể điều khiển qua App và hẹn giờ tự động bật tắt------------------------------------
 * Nội dung chương trình :
 * 1.Tại phần Setup.
 *    +Thực hiện các thao tác cài đặt như set Wifi, cài thư viện Firebase, cho phép ngắt, gán các giá trị ban đâu,...
 * 2.Phần Chương trình ngắt.
 *    +Chương trình ngắt ở sườn xuống ở 3 nút nhấn khai báo là itr1,itr2,itr3
 *    +Khi ngắt xảy ra với ngắt nào thì sẽ được gắn với van đó. 3 nút nhấn này để có thể điều khiển trực tiếp tại Device
 * 3.Chương trình chính(Vòng lặp loop).
 *    +Chương trình Lắng nghe khi database ở Server có sự thay đổi về trạng thái van để điều khiển 3 van
 *    +Cứ 10s thì sẽ xem thử báo thức có được bật không. Nếu bật thì lấy giá trị giờ và phút bật tắt của từng van về so sánh
 *    với giờ của server NTPtime của châu Á. Từ đó điều chỉnh trạng thái của Van cho phù hợp-->>Sau đó gửi lên trạng thái 3 van 
 *    lên databaseRealtime của Firebase. Sau đó vòng lặp lại tiếp tục bắt đầu lại từ mục 2.
 * 
 * Các thư viện cần cài đặt cho Hệ thông
  +Thư viện ESP8266WiFi
  +Tải thư viện của FirebaseArduino :  https://github.com/FirebaseExtended/firebase-arduino
  +Thư viện Arduino Json 5.x        :  https://github.com/bblanchon/ArduinoJson/tree/5.x
  +Thư viện của NTPtimeESP          :  https://github.com/SensorsIot/NTPtimeESP
  +Tìm server cho NTPtimeESP        :  https://www.pool.ntp.org/zone/vn
*/
#include <ESP8266WiFi.h>                // thêm thư viện ESP8266 vào để có thể thao tác với ESP8266
#include <FirebaseArduino.h>            // thêm thư viện của Firebase vào để liên kết với Firebase
#include <NTPtimeESP.h>                 // Thêm thư viện NTP vào để lấy thời gian trên mạng
  NTPtime NTPch("asia.pool.ntp.org");   // Server NTP của VietNam hoặc của Châu Á có 4 server
    /*
       server 0.asia.pool.ntp.org
       server 1.asia.pool.ntp.org
       server 2.asia.pool.ntp.org
       server 3.asia.pool.ntp.org
       Nếu không được thì thử server này "ch.pool.ntp.or"
    */
  strDateTime dateTime; 
  
#define FIREBASE_HOST "smartvalve-beffe.firebaseio.com" //Thay bằng địa chỉ firebase của bạn
#define FIREBASE_AUTH ""   //Không dùng xác thực nên không đổi
#define WIFI_SSID "F321"   //Thay wifi và mật khẩu
#define WIFI_PASSWORD "23456789"

#define itr1 D2 //04
#define itr2 D3 //00
#define itr3 D4 //02

#define VAN1 D6  //12
#define VAN2 D7  //13
#define VAN3 D8  //15

String IDthietbi="DHBH1999" ;  //********* Do nhà cung cấp quyết định */////////////////
int keyDevice= 1 ;

boolean stsVan1,stsVan2,stsVan3,stsVan1_Old,stsVan2_Old,stsVan3_Old;

unsigned long previousMillis; //-----Set thời gian ban đầu----------
const long interval = 10000;   //-----Set thời gian sau 10s sẽ lấy giá trị giờ và phút từ mạng-----
String H,M,Hb1,Mb1,Ht1,Mt1,Hb2,Mb2,Ht2,Mt2,Hb3,Mb3,Ht3,Mt3;        //-----Khai báo kiểu dữ liệu cho giờ và phút--------------

void ICACHE_RAM_ATTR BatSuKien1();
void ICACHE_RAM_ATTR BatSuKien2();
void ICACHE_RAM_ATTR BatSuKien3();
/*----------------------------------------------------------------------------------------------------*/
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  
  pinMode(itr1, INPUT);
  pinMode(itr2, INPUT);
  pinMode(itr3, INPUT);
  
  pinMode(VAN1, OUTPUT);
  pinMode(VAN2, OUTPUT);
  pinMode(VAN3, OUTPUT);
  
  stsVan1=false;
  stsVan2=false;
  stsVan3=false;
  
  // Kết nối wifi.
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("connecting");
    while (WiFi.status() != WL_CONNECTED) 
        {
          Serial.print(".");
          delay(500);
        }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH); //--- Bắt đầu Firebase---------//
  /*---------------------Kiểm tra Firebase hoạt động ổn chưa---------------------*/
  if (Firebase.failed()) 
      {
        Serial.println("streaming error");
        Serial.println(Firebase.error());
      }
  /*------------Bắt đầu stream thiết bị------------------------------------------*/
  Firebase.stream("/"+IDthietbi);  // Hàm này để khởi tạo theo dõi database ( Để xem có sự thay đổi nào trong database không)
  
  //-------Bật ngắt ở các chân itr1,itr2,itr3---------------------
  attachInterrupt(digitalPinToInterrupt(itr1),BatSuKien1, FALLING);
  attachInterrupt(digitalPinToInterrupt(itr2),BatSuKien2, FALLING);
  attachInterrupt(digitalPinToInterrupt(itr3),BatSuKien3, FALLING);
  /*----Trạn thái lần cuối nhất của 3 van gắn vào ----------------*/
  stsVan1_Old=stsVan1;
  stsVan2_Old=stsVan2;
  stsVan3_Old=stsVan3;
  /*-----Cho 3 van lúc đầu là đều OFF hết------------------------*/
  digitalWrite(VAN1,0);
  digitalWrite(VAN2,0);
  digitalWrite(VAN3,0);
  /*Kiem tra lúc đầu đã có thiết bị này trên firebase chưa*/
  /*Chưa không có thì cập nhật bằng hàm SetParameter, nếu có thì bỏ qua--------------------------------*/
  SetParameter();
  Serial.println("Set Up Finish");
  /*-----------------------------------------------------------------------------------------------------*/
  previousMillis=0; //Set thời gian ban đầu chạy chương trình.
}
/*-----------------------------------------------------------------------------------------------------------------*/
void loop() 
  {
        /*--------------Cập nhật khi database có sự thay đổi------------------------------*/  
          Get_and_Control();
        /*---------------Cứ 10s thì vào xem thử hẹn giờ có được bật không-------------------*/
          if ((unsigned long) ( millis() - previousMillis ) >= interval)  // (Nếu Tchươngtrinh_hiệntại - previousMillis) >=10 000  thì thực hiện câu lệnh dưới
                                                                          //Tức là cứ 10s thì sẽ thực hiện những câu lệnh dưới này.
              {
                previousMillis = millis(); //cập nhật lại thời gian cho biến previousMillis
                GetInfoTime();  // Xem thử Báo thức các Van được bật không. Nếu có thì thực hiện việc lấy trạng thái cho Van thông qua giờ hẹn
              }
         /*---------Kiểm tra dữ liệu Van và gửi lên Database của Firebase-----------------*/
          Check_Status_Valve(); 
  }


  
//------------------------------CÁC CHƯƠNG TRÌNH CON----------------------------------------
void SetParameter()
  {
    /* Nhiệm vụ :
      1.Gửi địa chỉ ID thiết bị kèm tên thiết bị ảo
      2.Set trạng thái các van lúc đầu đều là OFF.
      3.Set trạng thái hẹn giờ của 3 van đều là False.
      4.Set thời gian hẹn giờ lúc đầu là 00:00 cho tất cả các Van
      */
      
      /*--------Nhiệm vụ 1_Thiết lập tên ảo cho thiết bị-----------*/
      Firebase.setString(""+IDthietbi+"/name","Device1");
      /*---------Nhiệm vụ 2_Set trạng thái các Van lúc đầu LÀ OFF--------*/
      Firebase.setString(""+IDthietbi+"/VAN1/Status","OFF");
      Firebase.setString(""+IDthietbi+"/VAN2/Status","OFF");
      Firebase.setString(""+IDthietbi+"/VAN3/Status","OFF");
      /*---------Nhiệm vụ 3_Set trạng thái hẹn giờ lúc đầu của 3 van là OFF----------*/
      Firebase.setString(""+IDthietbi+"/VAN1/stsSW","OFF");
      Firebase.setString(""+IDthietbi+"/VAN2/stsSW","OFF");
      Firebase.setString(""+IDthietbi+"/VAN3/stsSW","OFF");
      /*---------Nhiem vu 4_Set trạng thái hẹn lúc đầu của 3 van------------ */
      Firebase.setString(""+IDthietbi+"/VAN1/Hour_B","0");
      Firebase.setString(""+IDthietbi+"/VAN1/Hour_T","0");
      Firebase.setString(""+IDthietbi+"/VAN1/Minute_B","0");
      Firebase.setString(""+IDthietbi+"/VAN1/Minute_T","0");

      Firebase.setString(""+IDthietbi+"/VAN2/Hour_B","0");
      Firebase.setString(""+IDthietbi+"/VAN2/Hour_T","0");
      Firebase.setString(""+IDthietbi+"/VAN2/Minute_B","0");
      Firebase.setString(""+IDthietbi+"/VAN2/Minute_T","0");

      Firebase.setString(""+IDthietbi+"/VAN3/Hour_B","0");
      Firebase.setString(""+IDthietbi+"/VAN3/Hour_T","0");
      Firebase.setString(""+IDthietbi+"/VAN3/Minute_B","0");
      Firebase.setString(""+IDthietbi+"/VAN3/Minute_T","0");
    }
void BatSuKien1()
{ 
    /*NV: Thay đổi trạng thái của Van1 khi nút nhấn 1 được nhấn*/
    delayMicroseconds(15000);
    while(digitalRead(itr1)==0);
    delayMicroseconds(15000); 
    stsVan1=!stsVan1;
    Serial.print("STATUS VAN1 : ");
    Serial.println(stsVan1);
  }
void BatSuKien2()
{ 
  /*NV: Thay đổi trạng thái của Van2 khi nút nhấn 2 được nhấn*/
    delayMicroseconds(15000);
    while(digitalRead(itr2)==0);
    delayMicroseconds(15000);
    stsVan2=!stsVan2;
    Serial.print("STATUS VAN2 : ");  
    Serial.println(stsVan2);
  }
void BatSuKien3()
{ 
    /*NV: Thay đổi trạng thái của Van3 khi nút nhấn 3 được nhấn*/
    delayMicroseconds(15000);
    while(digitalRead(itr3)==0);
    delayMicroseconds(15000);
    stsVan3=!stsVan3;
    Serial.print("STATUS VAN3 : ");  
    Serial.println(stsVan3);
  }

/*-----------------------------------------------------------------*/  
void GetInfoTime()
  {
      dateTime = NTPch.getNTPtime(7.0, 0);
      if(dateTime.valid) // Kiem tra dateTime.valid truoc khi dua ra gia tri thoi gian
        {
          //NTPch.printDateTime(dateTime);
          Serial.print("Gio :");
          H = dateTime.hour;      // lấy Giờ trực tuyến
          Serial.print(H);
          
          Serial.print(",Phut :");
          M = dateTime.minute;    // lấy Phút trực tuyến
          Serial.println(M);
          
          if ((Firebase.getString(""+IDthietbi+"/VAN1/stsSW"))== ("ON"))
            {
              GetInfo_HandM_DB("VAN1");
              }
          else 
          if ((Firebase.getString(""+IDthietbi+"/VAN2/stsSW"))== ("ON"))
            {
              GetInfo_HandM_DB("VAN2");
              }
          else
          if ((Firebase.getString(""+IDthietbi+"/VAN3/stsSW"))== ("ON"))
            {
              GetInfo_HandM_DB("VAN3");
              }
        }
      }
/*-----------------------------------------------------------*/
void GetInfo_HandM_DB(String van)
  {
    
    // Lấy giá trị giờ báo thức (tắt và mở ) của từng van từ Database
    String Hb= Firebase.getString(""+IDthietbi+"/" + van + "/Hour_B");
    String Mb= Firebase.getString(""+IDthietbi+"/" + van + "/Minute_B");

    String Ht= Firebase.getString(""+IDthietbi+"/" + van + "/Hour_T");
    String Mt= Firebase.getString(""+IDthietbi+"/" + van + "/Minute_T");

    //------------Lấy dữ liệu giờ và phút bật tắt van-------------------------------
    if( van == "VAN1")
    {
        Hb1=Hb;
        Mb1=Mb;
        Ht1=Ht;
        Mt1=Mt;
        Check_Update("VAN1");
      }
      if( van == "VAN2")
    {
        Hb2=Hb;
        Mb2=Mb;
        Ht2=Ht;
        Mt2=Mt;
        Check_Update("VAN2");
      }
      if( van == "VAN3")
    {
        Hb3=Hb;
        Mb3=Mb;
        Ht3=Ht;
        Mt3=Mt;
        Check_Update("VAN3");
      }
    
    }  
/*----------------Hàm kiểm tra báo thức----------------------------------------*/  
void Check_Update(String van)
  {
    /*-----------Kiểm tra báo thức bật van 1--------------------*/
    if(van == "VAN1")
    {
        if (Hb1==H)
          {
            if (Mb1==M)
              {
                 stsVan1 = true; // bật Van1
                 //---sau đó gửi dữ liệu status cua van 1 lên server-----------//
                 //Firebase.setString(""+IDthietbi+"/VAN1/Status","ON");  
                }
            }
     //-----Kiểm tra báo thức tắt van 1------------   
         if (Ht1==H)
          {
            if (Mt1==M)
              {
                 stsVan1 = false; // tắt Van1
                 //---sau đó gửi dữ liệu status cua van 1 lên server-----------//
                 //Firebase.setString(""+IDthietbi+"/VAN1/Status","OFF");
                }
            }
    }
   /*----------------------------Kiểm tra báo thức bật van 2----------------------------------*/
   if (van == "VAN2")
   {
    if (Hb2==H)
      {
        if (Mb2==M)
          {
             stsVan2 = true; // bật Van2
             //---sau đó gửi dữ liệu status cua van 2 lên server-----------//
             //Firebase.setString(""+IDthietbi+"/VAN2/Status","ON");
            }
        }
     //-----Kiểm tra báo thức tắt van 2------------   
     if (H==Ht2)
      {
        if (M==Mt2)
          {
             stsVan2 = false; // tắt Van2
             //---sau đó gửi dữ liệu status cua van 2 lên server-----------//
             //Firebase.setString(""+IDthietbi+"/VAN2/Status","OFF");
            }
        }
   }
   /*-----------------------Kiểm tra báo thức bật van 3-------------------------------*/
    if (van == "VAN3")
    {
    if (H==Hb3)
      {
        if (M==Mb3)
          {
             stsVan3 = true; // bật Van3
             //---sau đó gửi dữ liệu status cua van 3 lên server-----------//
             //Firebase.setString(""+IDthietbi+"/VAN3/Status","ON");
            }
        }
     //-----Kiểm tra báo thức tắt van 3------------   
     if (H==Ht3)
      {
        if (M==Mt3)
          {
             stsVan3 = false; // tắt Van3
             //---sau đó gửi dữ liệu status cua van 3 lên server-----------//
             //Firebase.setString(""+IDthietbi+"/VAN3/Status","OFF");
            }
        }    
    }
  }
/*---------Kiểm tra trạng thái và gửi dữ liệu van lên server-----------------*/
void Check_Status_Valve()
  {
    //----Kiểm tra để thay đổi trạng thái van-----------
    if ( stsVan1 != stsVan1_Old)
      {
        if (stsVan1 ==true)
          {
            Firebase.setString(""+IDthietbi+"/VAN1/Status","ON");
          }
          else
          {
            Firebase.setString(""+IDthietbi+"/VAN1/Status","OFF");
            }
         stsVan1_Old=stsVan1;   
        }

      if ( stsVan2 != stsVan2_Old)
      {
        if (stsVan2 ==true)
          {
            Firebase.setString(""+IDthietbi+"/VAN2/Status","ON");
          }
          else
          {
            Firebase.setString(""+IDthietbi+"/VAN2/Status","OFF");
            }
        stsVan2_Old=stsVan2;
        }

       if ( stsVan3 != stsVan3_Old)
      {
        if (stsVan3 ==true)
          {
            Firebase.setString(""+IDthietbi+"/VAN3/Status","ON");
          }
          else
          {
            Firebase.setString(""+IDthietbi+"/VAN3/Status","OFF");
            }
          stsVan3_Old=stsVan3;
        }
    }

/*---Hàm cập nhật giá trị khi có sự thay đổi ở DATABASE------*/
void Get_and_Control()
  {
    if (Firebase.failed()) {
      Serial.println("streaming error");
      Serial.println(Firebase.error());
    }
  if (Firebase.available())
  {
      FirebaseObject event = Firebase.readEvent();
      String eventType = event.getString("type");
      eventType.toLowerCase();
    
      Serial.print("event: ");
      Serial.println(eventType);
      if (eventType == "put") {
      Serial.print("data:");
      Serial.println(event.getString("data"));
      Serial.print("path:");
      String path = event.getString("path");
      Serial.println(path);
      String data = event.getString("data");
      
      if (path == ("/VAN1/Status"))
                    {
                      String payload = event.getString("data");
                      if (payload == "ON")
                        digitalWrite(VAN1,1);
                      else digitalWrite(VAN1,0);
                      Serial.print("Status Van1: ");
                      Serial.println(payload);
                    }
        if (path == ("/VAN2/Status"))
                    {
                      String payload = event.getString("data");
                      if (payload == "ON")
                        digitalWrite(VAN2,1);
                      else digitalWrite(VAN2,0);
                      Serial.print("Status Van2: ");
                      Serial.println(payload);
                    }
         if (path == ("/VAN3/Status"))
                    {
                      String payload = event.getString("data");
                      if (payload == "ON")
                        digitalWrite(VAN3,1);
                      else digitalWrite(VAN3,0);
                      Serial.print("Status Van3: ");
                      Serial.println(payload);
                    }
        }
  }
}   
