const Http = require('http');
const SerialPort = require('serialport');
// const serialport = new SerialPort("/dev/cu.wchusbserial14140");
const serialport = new SerialPort("/dev/ttyUSB0");

// Connection parameters
const HOST_URI = 'localhost';
const HOST_PORT = '3001';

// Data types
const DHT = 0;

function post_request(data, path) {
      let options = {
            host: HOST_URI,
            path: '/api' + path,
            port: HOST_PORT,
            method: 'POST',
            headers: {
                  'Content-Type': 'application/json',
            }
      };

      var req = Http.request(options, function (res) {
            console.log('Status: ' + res.statusCode);
            //console.log('Headers: ' + JSON.stringify(res.headers));
            res.setEncoding('utf8');
            res.on('data', function (body) {
                  console.log('Body: ' + body);
            });
      });
      req.on('error', function (e) {
            console.log('problem with request: ' + e.message);
      }); 

      // write data to request body
      req.write(JSON.stringify(data));
      req.end();
}

serialport.on('readable', function () {
      let data = serialport.read();
      console.log(data.toString());
      let dataArray = data.toString().split("_");

      if (dataArray.length > 1) {
            switch (parseInt(dataArray[0])) {
                  case DHT:
                        let dht = {
                              u: dataArray[1],
                              h: dataArray[2],
                              t: dataArray[3]
                        };

                        post_request(dht, '/dht');
                        break;

                  default:
                        console.warn('Data type not recognize!!', data);
                        break;
            }
      }

});