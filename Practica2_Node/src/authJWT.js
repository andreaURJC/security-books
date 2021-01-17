const jwt = require('jsonwebtoken');
const config = require('./config');
const {User} = require('./models/user');
const {Role} = require('./models/role');


const verifyToken = async (req, res, next) => {
    let token = req.headers['authorization'];

    if (!token) {
        return res.status(403).send({auth: false, message: 'No token provided.'});
    }
    try {
        token = token.replace("Bearer ", "");
        console.log(token);
        let decoded = await jwt.verify(token, config.SECRET);
        req.nick = decoded.id;
        next();
    } catch (e) {
        return res.status(500).send({auth: false, message: 'Failed to authenticatetoken.'});
    }
}

const isAdmin = async (req, res, next) => {
    const user = await User.findOne({nick:req.nick});

    const roles = await Role.find({_id: {$in: user.roles}});

    for (role of roles) {
        if (role.name === "ADMIN") {
            next();
            return;
        }
        return res.status(403).send({message: 'User doesnt have permission to access'});
    }
}
module.exports = {verifyToken, isAdmin};