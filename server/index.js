var SerialPort = require("serialport");
var serialport = new SerialPort("/dev/cu.wchusbserial14530");
/*serialport.on('open', function(){
  console.log('Serial Port Opend');
  serialport.on('data', function(data){
      console.log(data[0]);
  });
});*/
serialport.on('readable', function () {
  var data = serialport.read();
  var dataArray = data.toString().split("_");
  console.log('Umidità: ', dataArray[0]);
  console.log('Temperatura: ', dataArray[1]);
});