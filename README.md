SMARTBEACH
----------
Your beach in your wirst.

**PRE-REQUISITE**
A valid installation of nodejs must be installed.

**INSTALLATION SERVER NODEJS**
Go to main folder and run
```javascript
npm install
```
after that there is are  the command to start system.
```javascript
npm start
```
This will run API locally. To expose it you have to run 
```javascript
npm run expose
```
Now server is running and ready to process data.

**INSTALLATION FISHINO**
Once fishino is connected to the USB port till the server. Now USB serial identifyers must be write inside file *serverArduino.js*. 

The following line must be update with correct serial port
```javascript
const serialport = new SerialPort("/dev/ttyUSB0");
```

After that Arduino can be launched for testing with 
```shell
npm run arduino
```

**INSTALLATION SMARTBEACH**
Before running app a line must be edited in Android Studio inside file *MainActivity.java*
```java
public static final String URL = "http://76e3f948.ngrok.io/api";
```

Copy url write in console when expose API and paste it in there.

**ENJOY BEACHEES**
