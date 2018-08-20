const Express = require('express');
const SerialPort = require("serialport");
const Datastore = require('nedb');
const app = Express();
const router = Express.Router();
const Route = require('./API/routes/route');

const port = process.env.PORT || 3001;
const serialport = new SerialPort("/dev/cu.wchusbserial14140");

// DATABASE
let lidoBase = {};
lidoBase.dht = new Datastore('lidoBase.db');
lidoBase.dht.loadDatabase();

// Expose API on localhost:3001
app.get('/', (request, response) => response.send('Beachees Main Server'));

// all routes prefixed with /api
app.use('/api', router);

Route(router, lidoBase);

// set the server to listen on port 3001
app.listen(port, () => console.log('Listening on port', port));

serialport.on('readable', function () {
   let data = serialport.read();
   let dataArray = data.toString().split("_");

   let dht = {
      humidity: dataArray[0],
      temperature: dataArray[1],
      date: new Date()
   };

   lidoBase.dht.insert(dht, function (err, newDoc) {});
});