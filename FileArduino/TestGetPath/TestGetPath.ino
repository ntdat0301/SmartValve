#include <ESP8266WiFi.h>                // thêm thư viện ESP8266 vào để có thể thao tác với ESP8266
#include <FirebaseArduino.h>  
#define FIREBASE_HOST "smartvalve-beffe.firebaseio.com" //Thay bằng địa chỉ firebase của bạn
#define FIREBASE_AUTH ""   //Không dùng xác thực nên không đổi
#define WIFI_SSID "F321"   //Thay wifi và mật khẩu
#define WIFI_PASSWORD "23456789"
String IDthietbi="DHBH1999" ;  //********* Do nhà cung cấp quyết định */////////////////
int keyDevice= 1 ;
//FirebaseData firebaseData;
void setup() {
  // put your setup code here, to run once:
Serial.begin(9600);
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
}

void loop() {
  // put your main code here, to run repeatedly:
    String abb= Firebase.getKey();
    Serial.println(abb);
    delay(2000);
}
