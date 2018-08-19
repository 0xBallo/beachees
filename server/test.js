const express = require('express');
const app = express();
const router = express.Router();
const Route = require('./API/routes/route');
const port = process.env.PORT || 3000;

// from top level path e.g. localhost:3000, this response will be sent
app.get('/', (request, response) => response.send('Beachees Main Server'));

// all routes prefixed with /api
app.use('/api', router);

Route(router);

// set the server to listen on port 3000
app.listen(port, () => console.log('Listening on port', port));