const mongoose = require('mongoose');

const isValidEmail = function(email) {
    let mailformat = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;

    return typeof email == 'string' && email != "" && mailformat.test(email);
}

const userSchema = new mongoose.Schema({
    nick: {
        type: String,
        required: [true, 'Nick is mandatory'],
        unique: true
    },
    password: {
        type: String,
        required: [true, 'Password is mandatory']
    },
    email: {
        type: String,
        validate: {
            validator: isValidEmail,
            message: props => `${props.value} is not a valid email`
        },
        required: [true, 'Email is mandatory']
    },
    roles: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Role'
    }]
});

const User = mongoose.model('User', userSchema);

function toResponse(document) {

    if (document instanceof Array) {
        return document.map(elem => toResponse(elem));
    } else {
        let response = document.toObject({ versionKey: false });
        response.id = response._id.toString();
        delete response._id;
        return response;
    }
}

module.exports = { User, userSchema, isValidEmail, toResponse }
