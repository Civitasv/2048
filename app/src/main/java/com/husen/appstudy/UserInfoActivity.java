package com.husen.appstudy;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.husen.application.MyApplication;
import com.husen.entity.User;
import com.husen.greendao.UserDao;
import com.husen.utils.ToastUtil;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class UserInfoActivity extends AppCompatActivity {

    private AlertDialog.Builder builder;
    private int choice = -1;
    ItemView nickName;
    ItemView sex;
    ItemView signName;
    ItemView record;
    ItemView phone;
    ItemView version;
    ImageView h_back;
    ImageView h_head;
    private Uri imageUri;
    private Uri headUri;
    private Bitmap head;// 头像Bitmap
    public User user;
    //我感觉相当于resultSet
    private Cursor cursor;
    public static final String TAG = "DaoExample";
    // sd路径
    private static String path = "/sdcard/myHead/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_info);
        user = findUser();
        initView();
        if (savedInstanceState != null) {
            nickName.setRightDesc(savedInstanceState.getString("nickName"));
            sex.setRightDesc(savedInstanceState.getString("sex"));
            signName.setRightDesc(savedInstanceState.getString("signName"));
            headUri = Uri.parse(savedInstanceState.getString("head_uri"));
        } else {
            Resources r = this.getResources();
            headUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + r.getResourcePackageName(R.drawable.user) + "/"
                    + r.getResourceTypeName(R.drawable.user) + "/"
                    + r.getResourceEntryName(R.drawable.user));
        }
        // 找到按钮控件
        ItemView backMain = findViewById(R.id.back);

        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                startActivity(intent);
                UserInfoActivity.this.finish();
            }
        };

        backMain.setOnClickListener(btnClickListener);
        if(user!=null&&user.getUri()!=null&&!"".equals(user.getUri())){
            Glide.with(this).load(Uri.parse(user.getUri()))
                    .bitmapTransform(new BlurTransformation(this, 25), new CenterCrop(this))
                    .into(h_back);

            Glide.with(this).load(Uri.parse(user.getUri()))
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(h_head);
        }else{
            Glide.with(this).load(headUri)
                    .bitmapTransform(new BlurTransformation(this, 25), new CenterCrop(this))
                    .into(h_back);

            Glide.with(this).load(headUri)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(h_head);
        }
        // 初始化user
        if(user == null) {
            user = new User(null, nickName.getRightDesc().toString(), signName.getRightDesc().toString(),
                    sex.getRightDesc().toString(), phone.getRightDesc().toString(), ChessUtil.maxScore,ChessUtil.score, null,headUri.toString());
            // 存入数据库
            getUserDao().insert(user);
        }
        changeHead();
        changeNickName();
        changeSignName();
        changeSex();
        changePhone();
        getVersion();
        getRecord();
    }


    private User findUser(){
        List<User> user =getUserDao().loadAll();
        // 返回第一个
        if(user!=null&&user.size()>0)
            return user.get(user.size()-1);
        return null;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("nickName", nickName.getRightDesc());
        outState.putString("sex", sex.getRightDesc());
        outState.putString("signName", signName.getRightDesc());
        outState.putString("head_uri", headUri.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
        startActivity(intent);
        UserInfoActivity.this.finish();
        super.onBackPressed();
    }

    /**
     * 修改头像
     */
    private void changeHead() {
        /*
        点击头像进行更换头像
         */
        h_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.h_head:// 更换头像
                        showTypeDialog();
                        break;
                }
            }
        });

    }

    /**
     * 显示对话框
     */
    private void showTypeDialog() {
        //显示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                //打开文件
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                dialog.dismiss();
            }
        });

        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    //如果文件存在则删除,创建一个新的文件
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(UserInfoActivity.this,
                            "com.husen.appstudy.fileProvider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                //      Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                startActivityForResult(intent, 2);// 采用ForResult打开
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }

                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    cropPhoto(imageUri);// 裁剪图片
                }

                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        /**
                         * 上传服务器代码
                         */
                        Uri uri = setPicToView(head);// 保存在SD卡中
                        Glide.with(this).load(uri)
                                .bitmapTransform(new BlurTransformation(this, 25), new CenterCrop(this))
                                .into(h_back);

                        Glide.with(this).load(uri)
                                .bitmapTransform(new CropCircleTransformation(this))
                                .into(h_head);
                        user.setUri(uri.toString());
                        getUserDao().update(user);
                    }
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 调用系统的裁剪功能
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setData(uri);
        //intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存图片
     *
     * @param mBitmap
     */
    private Uri setPicToView(Bitmap mBitmap) {
        Uri uri = null;
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return null;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+"head.jpg";// 图片名字
        File outputImage1 = new File(fileName);
        //如果文件存在则删除
        if (outputImage1.exists()) {
            outputImage1.delete();
        }
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            File outputImage = new File(fileName);
            uri = FileProvider.getUriForFile(UserInfoActivity.this,
                    "com.husen.appstudy.fileProvider", outputImage);
            headUri = uri;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return uri;
    }


    private UserDao getUserDao() {
        // 通过 BaseApplication 类提供的 getDaoSession() 获取具体 Dao
        return ((MyApplication) this.getApplication()).getDaoSession().getUserDao();
    }

    private SQLiteDatabase getDb() {
        // 通过 BaseApplication 类提供的 getDb() 获取具体 db
        return ((MyApplication) this.getApplication()).getDb();
    }


    /**
     * 修改昵称
     */
    private void changeNickName() {
        // 为修改信息添加事件监听器
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                // dialog
                final EditText editText = new EditText(UserInfoActivity.this);
                builder = new AlertDialog.Builder(UserInfoActivity.this).setIcon(R.drawable.ic_nick_name).setTitle("修改昵称").setView(editText)
                        .setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                nickName.setRightDesc(editText.getText().toString());
                                user.setNickName(editText.getText().toString());
                                getUserDao().update(user);
                            }
                        }).setNegativeButton("取消修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                builder.create().show();

            }
        };
        nickName.setOnClickListener(btnClickListener);
    }

    /**
     * 修改个性签名
     */
    private void changeSignName() {
        // 为修改信息添加事件监听器
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                // dialog
                final EditText editText = new EditText(UserInfoActivity.this);
                builder = new AlertDialog.Builder(UserInfoActivity.this).setIcon(R.drawable.ic_sign_name).setTitle("修改个性签名").setView(editText)
                        .setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                signName.setRightDesc(editText.getText().toString());
                                user.setSignName(editText.getText().toString());
                                getUserDao().update(user);
                            }
                        }).setNegativeButton("取消修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();

            }
        };
        signName.setOnClickListener(btnClickListener);
    }

    /**
     * 修改性别
     */
    private void changeSex() {
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                //默认选中第一个
                final String[] items = {"男", "女"};
                builder = new AlertDialog.Builder(UserInfoActivity.this).setIcon(R.drawable.ic_sex).setTitle("修改性别")
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                choice = i;
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (choice != -1) {
                                    sex.setRightDesc(items[choice]);
                                    user.setSex(items[choice]);
                                    getUserDao().update(user);
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();


            }
        };
        sex.setOnClickListener(btnClickListener);
    }


    /**
     * 修改手机
     */
    private void changePhone() {
        // 为修改信息添加事件监听器
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                // dialog
                final EditText editText = new EditText(UserInfoActivity.this);
                builder = new AlertDialog.Builder(UserInfoActivity.this).setIcon(R.drawable.ic_phone).setTitle("修改手机").setView(editText)
                        .setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                phone.setRightDesc(editText.getText().toString());
                                user.setPhone(editText.getText().toString());
                                getUserDao().update(user);
                            }
                        }).setNegativeButton("取消修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();

            }
        };
        phone.setOnClickListener(btnClickListener);
    }

    /**
     * 查看版本号
     */
    private void getVersion() {
        // 为修改信息添加事件监听器
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                // dialog
                builder = new AlertDialog.Builder(UserInfoActivity.this).setIcon(R.drawable.ic_about).setTitle("版本号")
                        .setMessage("versioncode:" + packageCode(UserInfoActivity.this) + "\n" + "versionname:" + packageName(UserInfoActivity.this)).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Toast.makeText(UserInfoActivity.this, "确定按钮", Toast.LENGTH_LONG).show();
                            }
                        }).setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Toast.makeText(UserInfoActivity.this, "关闭按钮", Toast.LENGTH_LONG).show();
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();

            }
        };
        version.setOnClickListener(btnClickListener);
    }
    /**
     * 查看记录
     */
    private void getRecord() {
        // 为修改信息添加事件监听器
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                // dialog
                builder = new AlertDialog.Builder(UserInfoActivity.this).setIcon(R.drawable.ic_pass).setTitle("最高纪录")
                        .setMessage(user.getMaxScore()+"").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Toast.makeText(UserInfoActivity.this, "确定按钮", Toast.LENGTH_LONG).show();
                            }
                        }).setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Toast.makeText(UserInfoActivity.this, "关闭按钮", Toast.LENGTH_LONG).show();
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();

            }
        };
        record.setOnClickListener(btnClickListener);
    }

    /**
     * 返回版本号
     *
     * @param context
     * @return
     */
    public static int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 获取版本名
     *
     * @param context
     * @return
     */
    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //顶部头像控件
        h_back = (ImageView) findViewById(R.id.h_back);
        h_head = (ImageView) findViewById(R.id.h_head);
        //下面item控件
        nickName = (ItemView) findViewById(R.id.nickName);
        sex = (ItemView) findViewById(R.id.sex);
        signName = (ItemView) findViewById(R.id.signName);
        record = (ItemView) findViewById(R.id.record);
        phone = (ItemView) findViewById(R.id.phone);
        version = (ItemView) findViewById(R.id.version);
        if(user == null)
            return;
        if(user.getNickName()!=null&&!"".equals(user.getNickName())){
            nickName.setRightDesc(user.getNickName());
        }
        if(user.getSex()!=null&&!"".equals(user.getSex())){
            sex.setRightDesc(user.getSex());
        }
        if(user.getSignName()!=null&&!"".equals(user.getSignName())){
            signName.setRightDesc(user.getSignName());
        }
        if(user.getPhone()!=null&&!"".equals(user.getPhone())){
            phone.setRightDesc(user.getPhone());
        }
        if(user.getMaxScore()!=0){
            record.setRightDesc(user.getMaxScore()+"");
        }
        if(user.getScore()!=0){
            record.setRightDesc(user.getScore()+"");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
