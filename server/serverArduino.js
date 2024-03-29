const Http = require('http');
const SerialPort = require('serialport');
const serialport = new SerialPort("/dev/cu.wchusbserial14530");
//const serialport = new SerialPort("/dev/ttyUSB0");

// Connection parameters
const HOST_URI = 'localhost';
const HOST_PORT = '3001';

// Data types
const DHT = 0;
const UVA = 1;
const WATER_T = 2;
const TURBIDITY = 3;
const ACC_GYRO = 4;

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
            let data = {}
            switch (parseInt(dataArray[0])) {
                  case DHT:
                        data = {
                              u: dataArray[1],
                              h: dataArray[2],
                              t: dataArray[3]
                        };

                        post_request(data, '/dht');
                        break;

                  case WATER_T:
                        data = {
                              t: dataArray[1]
                        };

                        post_request(dataW, '/sea/temp');
                        break;
                  case TURBIDITY:
                        data = {
                              t: dataArray[1]
                        };

                        post_request(dataTurb, '/sea/turbidity');
                        break;
                  case ACC_GYRO:
                        data = {
                              a: dataArray[1],
                              g: dataArray[2]
                        };

                        post_request(dataACC, '/sea/waves');
                        break;
                  case UVA:
                        data = {
                              u: dataArray[1],
                              l: dataArray[2]
                        };

                        post_request(data, '/uva');
                        break;

                  default:
                        console.warn('Data type not recognize!!', data);
                        break;
            }
      }

});