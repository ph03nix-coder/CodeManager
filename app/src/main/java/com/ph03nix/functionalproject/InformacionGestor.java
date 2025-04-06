package com.ph03nix.functionalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ph03nix.functionalproject.Database.GestorVentasDataSource;
import com.ph03nix.functionalproject.Security.UniqueCode;

public class InformacionGestor extends AppCompatActivity {

    GestorVentasDataSource dataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestor_information);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar); // Convierte el MaterialToolbar en ActionBar

        // Habilitar botón de retroceso
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Listener para el botón de navegación (flecha atrás)
        topAppBar.setNavigationOnClickListener(v -> {
            finish();
        });

        dataSource = new GestorVentasDataSource(getApplicationContext());
        dataSource.open();

        Intent intent = getIntent();
        int id = intent.getIntExtra("identidad", 0);
        GestorVentas gestor = dataSource.getGestorById(id);

        Button btnRemove = findViewById(R.id.btn_delete);
        btnRemove.setOnClickListener(v -> {
            dataSource.deleteGestor(id);
            Toast.makeText(getApplicationContext(), "Gestor eliminado", Toast.LENGTH_SHORT).show();
            finish();
        });

        Button btnVerify = findViewById(R.id.btn_verify);
        btnVerify.setOnClickListener(v -> {
            String message = "";
            if(new UniqueCode().verificarCodigo(gestor.getUniqueCode(), gestor.getId())) {
                message = "Código verificado correctamente";
            } else {
                message = "El código no es válido";
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });

        TextView tvId = findViewById(R.id.giId);
        TextView tvName = findViewById(R.id.giName);
        TextView tvCode = findViewById(R.id.giUniqueCode);

        tvId.setText(Integer.toString(id));
        tvName.setText(gestor.getName());
        tvCode.setText(gestor.getUniqueCode());

        ImageView qrView = findViewById(R.id.iv_qr_code);
        Bitmap qrCode = generateQRCode(gestor.getUniqueCode(), 500, 500);
        if(qrCode != null) {
            qrView.setImageBitmap(qrCode);
        } else {
            // Handle error
        }
    }

    public Bitmap generateQRCode(String text, int width, int height) {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    width,
                    height
            );
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }

        // Convertir BitMatrix a Bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }
}
