const Express = require('express');
const Datastore = require('nedb');
const Parser = require("body-parser");
const app = Express();
const router = Express.Router();
const Route = require('./API/routes/route');

const port = process.env.PORT || 3001;

// DATABASE
let lidoBase = {};
lidoBase.beach = new Datastore('lidoBase-beach.db');
lidoBase.sea = new Datastore('lidoBase-sea.db');

lidoBase.sea.loadDatabase();
lidoBase.beach.loadDatabase();

// Expose API on localhost:3001
app.get('/', (request, response) => response.send('Beachees Main Server'));

app.use(Parser.json());

// all routes prefixed with /api
app.use('/api', router);

Route(router, lidoBase);

// set the server to listen on port 3001
app.listen(port, () => console.log('Listening on port', port));