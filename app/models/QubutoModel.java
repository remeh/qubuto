package models;

import models.User;

import com.mehteor.db.MongoModel;

/**
 * A QubutoModel is an object which has a creator and an ID.
 * This class is mainly existing for right tests.
 */
public abstract class QubutoModel extends MongoModel {
    abstract public User getCreator();
}
