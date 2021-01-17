const express = require('express');
const router = express.Router();
const { User, toResponse, isValidEmail } = require('../models/user.js');
const Book = require('../models/book.js').Book;
const mongoose = require('mongoose');
const {verifyToken} = require('../authJWT.js')

// TODO: Refactor move to Security package
const config = require('../config')
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');

const INVALID_USER_ID_RESPONSE = { "error": "Invalid user id" };
const USER_NOT_FOUND_RESPONSE = { "error": "User not found" };

router.get('/', verifyToken, async (req, res) => {
    const allUsers = await User.find().exec();
    res.json(toResponse(allUsers));
});

router.get('/:id', verifyToken, async (req, res) => {
    const id = req.params.id;

    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(400).send(INVALID_USER_ID_RESPONSE);
    }

    const user = await User.findById(id);
    if (!user) {
        return res.status(404).send(USER_NOT_FOUND_RESPONSE);
    }

    res.json(toResponse(user));

});

router.post('/', verifyToken, async (req, res) => {

    const result = await User.find({ nick: req.body.nick }).exec();
    if (result.length > 0) {
        return res.status(409).send({ "error": "Already exists a user with that nick" });
    }

    const user = new User({
        nick: req.body.nick,
        email: req.body.email
    });

    try {

        const savedUser = await user.save();
        res.json(toResponse(savedUser));

    } catch (error) {
        console.log(error);
        res.status(400).send(error);
    }
});

router.patch('/:id', verifyToken,async (req, res) => {
    const id = req.params.id;

    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(400).send(INVALID_USER_ID_RESPONSE);
    }

    if (!isValidEmail(req.body.email)) {
        return res.status(400).send({ "error": "Invalid email" });
    }

    const user = await User.findById(id);
    if (!user) {
        return res.status(404).send(USER_NOT_FOUND_RESPONSE);
    }

    user.email = req.body.email
    const updatedUser = await user.save();
    res.json(toResponse(updatedUser));

});

router.delete('/:id', verifyToken,async (req, res) => {
    const id = req.params.id;

    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(400).send(INVALID_USER_ID_RESPONSE);
    }

    const user = await User.findById(id);
    if (!user) {
        return res.status(404).send(USER_NOT_FOUND_RESPONSE);
    }

    const result = await Book.find({ "comments": { $elemMatch: { "user": id } } }).exec();
    if (result.length > 0) {
        return res.status(409).send({ "error": "Can't delete user because has associated comments" });
    }

    await User.findByIdAndDelete(id);

    res.json(toResponse(user));
});

router.get('/:id/comments', verifyToken, async (req, res) => {
    const id = req.params.id;

    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(400).send(INVALID_USER_ID_RESPONSE);
    }

    const user = await User.findById(id);
    if (!user) {
        return res.status(404).send(USER_NOT_FOUND_RESPONSE);
    }

    const userComments = await Book.aggregate([
        { $unwind: "$comments" },
        { $match: { "comments.user": user._id } },
        {
            $project: {
                "_id": 0,
                "id": "$comments._id",
                "bookId": "$_id",
                "comment": "$comments.comment",
                "score": "$comments.score"
            }
        },
    ]);

    res.json(userComments);

});

router.post('/login', async (req, res) => {
    const { nick, password } = req.body;
    
    try {
        const user = await User.findOne({ "nick": nick }).populate("roles");
        console.log(user)
        if (!user) {
            return res.status(400).send("Nick/Password combination is incorrect")
        }

        let isPasswordCorrect = bcrypt.compareSync(password, user.password);
        if (!isPasswordCorrect) {
            return res.status(400).send("Nick/Password combination is incorrect")
        }

        let token = jwt.sign({ id: user.nick }, config.SECRET, {
            expiresIn: 86400 // expires in 24 hours
        });
        token = "Bearer " + token;

        res.send({ token })
    } catch (e) {
        res.status(500).send(e);
    }
});

module.exports = router;