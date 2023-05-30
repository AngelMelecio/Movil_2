package com.example.claseej23bcanslogin

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.model.ShareVideo
import com.facebook.share.model.ShareVideoContent
import com.facebook.share.widget.ShareDialog
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays


class MainActivity : AppCompatActivity() {
    var callbackManager = CallbackManager.Factory.create();
    var shareDialog: ShareDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //se instancia la ventana de compartir contenido
        shareDialog = ShareDialog(this)

        try {
            val info = packageManager.getPackageInfo(
                "com.example.claseej23bcanslogin",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }


        //boton de compartir link
        var btnlink = findViewById<View>(R.id.btnlink) as Button
        var btnImage = findViewById<View>(R.id.btnSharePhoto) as Button
        val btnVideo = findViewById<Button>(R.id.btnShareVideo)

        btnVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 456)
        }
        btnlink.setOnClickListener {
            val content: ShareLinkContent = ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://github.com/ElCesar28"))
                .build()
            if (ShareDialog.canShow(ShareLinkContent::class.java)){
                shareDialog!!.show(content)
            }
        }

        // Lanzar el intent cuando se haga click en el botón
        btnImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 123)
        }

        //COSAS PARA EL LOGIN
        val EMAIL = "email"

        var loginButton = findViewById<View>(R.id.login_button) as LoginButton
        loginButton.setReadPermissions(Arrays.asList(EMAIL))

        // Callback registration
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })


        callbackManager = create()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                }

                override fun onCancel() {
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    // App code
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        // Sobrescribir onActivityResult para manejar el resultado
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
                val imageUri = data.data

                // Convertir la Uri de la imagen en un Bitmap
                val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

                // Crear SharePhoto
                val photo = SharePhoto.Builder()
                    .setBitmap(imageBitmap)
                    .build()

                // Crear SharePhotoContent
                val content = SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build()

                // Comprobar si podemos compartir contenido de tipo SharePhotoContent y, si podemos, mostrar el diálogo
                if (ShareDialog.canShow(SharePhotoContent::class.java)) {
                    shareDialog?.show(content)
                }
            }
        if (requestCode == 456 && resultCode == RESULT_OK && data != null) {
            val videoUri = data.data

            val video = ShareVideo.Builder()
                .setLocalUrl(videoUri)
                .build()

            val content = ShareVideoContent.Builder()
                .setVideo(video)
                .build()

            if (ShareDialog.canShow(ShareVideoContent::class.java)) {
                shareDialog?.show(content)
            }
        }

    }
}