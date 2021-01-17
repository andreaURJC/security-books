const mongoose = require('mongoose');

const roleSchema = new mongoose.Schema({
    name: {
        type: String,
        required: [true, 'Name is mandatory'],
        unique: true
    }
});

const Role = mongoose.model('Role', roleSchema);

module.exports = { Role, roleSchema }