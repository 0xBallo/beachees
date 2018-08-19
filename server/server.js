var SerialPort = require("serialport");
var serialport = new SerialPort("/dev/cu.wchusbserial14140");

var Datastore = require('nedb');
db = {};
db.lidoBase = new Datastore('lidoBase.db');
db.lidoBase.loadDatabase();

var dht;

serialport.on('readable', function () {
  var data = serialport.read();
  var dataArray = data.toString().split("_");
  //console.log('Umidità: ', dataArray[0]);
  dht = {humidity: dataArray[0],
            temperature: dataArray[1]};
  //console.log('Temperatura: ', dataArray[1]);
  db.lidoBase.insert(dht, function (err, newDoc){});
  //var result;
  //db.prova.find({}, function (err, dht) { result = dht.temperature});
  //console.log('result: ', result);
});
