package com.husen.daoImpl;

import com.husen.application.MyApplication;
import com.husen.entity.User;
import com.husen.greendao.DaoSession;
import com.husen.greendao.UserDao;

import org.greenrobot.greendao.internal.DaoConfig;

public class UserDaoImpl extends UserDao {
    public UserDaoImpl(DaoConfig config) {
        super(config);
    }

    @Override
    public long insert(User entity) {
        return super.insert(entity);
    }

    @Override
    public long insertOrReplace(User entity) {

        return super.insertOrReplace(entity);
    }
}
