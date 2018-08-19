const Express = require('express');
const SerialPort = require("serialport");
const Datastore = require('nedb');
const app = Express();
const router = Express.Router();
const Route = require('./API/routes/route');

const port = process.env.PORT || 3001;
const serialport = new SerialPort("/dev/cu.wchusbserial14140");

// DATABASE
let db = {};
db.lidoBase = new Datastore('lidoBase.db');
db.lidoBase.loadDatabase();

// Expose API on localhost:3001
app.get('/', (request, response) => response.send('Beachees Main Server'));

// all routes prefixed with /api
app.use('/api', router);

Route(router, db.lidoBase);

// set the server to listen on port 3001
app.listen(port, () => console.log('Listening on port', port));

serialport.on('readable', function () {
   let data = serialport.read();
   let dataArray = data.toString().split("_");

   let dht = {
      humidity: dataArray[0],
      temperature: dataArray[1]
   };

   db.lidoBase.insert(dht, function (err, newDoc) {});
});