const jwt = require('jsonwebtoken');
const config = require('./config');

async function verifyToken(req, res, next) {
    let token = req.headers['authorization'];

    if (!token) {
        return res.status(403).send({auth: false, message: 'No token provided.'});
    }
    try {
        token = token.replace("Bearer ", "");
        console.log(token);
        let decoded = await jwt.verify(token, config.SECRET);
        req.userId = decoded.id;
        next();
    } catch (e) {
        return res.status(500).send({auth: false, message: 'Failed to authenticatetoken.'});
    }
}

module.exports = verifyToken;