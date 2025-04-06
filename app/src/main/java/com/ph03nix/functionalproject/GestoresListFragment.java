package com.ph03nix.functionalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class GestoresListFragment extends Fragment implements GestorVentasAdapter.OnGestorClickListener {

    private GestorVentasDataSource dataSource;
    private GestorVentasAdapter adapter;
    private FloatingActionButton fab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = new GestorVentasDataSource(requireContext().getApplicationContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("FragmentLifecycle", "onCreateView ejecutándose");
        View view = inflater.inflate(R.layout.fragment_gestores_list, container, false);

        // Configurar RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewGestores);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GestorVentasAdapter(new ArrayList<>(), this::onGestorClick);
        recyclerView.setAdapter(adapter);

        // Configurar FAB
        fab = view.findViewById(R.id.fabAddGestor);
        fab.setOnClickListener(v -> showAddGestorDialog());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataSource.open();
        loadGestores();
    }

    private void loadGestores() {
        new Thread(() -> {
            List<GestorVentas> gestores = dataSource.getAllGestores();
            requireActivity().runOnUiThread(() -> adapter.updateData(gestores));
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadGestores();
    }

    @Override
    public void onGestorClick(GestorVentas gestor) {
        // Mostrar detalles del gestor seleccionado
        Intent intent = new Intent(getActivity(), InformacionGestor.class);
        intent.putExtra("identidad", gestor.getId());
        intent.putExtra("nombre", gestor.getName());
        intent.putExtra("codigo", gestor.getUniqueCode().getBase64());
        startActivityForResult(intent, 0);
    }

    private void showAddGestorDialog() {
        Log.e("DEBUG", "showAddGestorDialog: invoked");
        // Inflar el layout del diálogo
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_gestor, null);

        // Obtener referencias a los campos
        
        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombre);

        // Crear el diálogo
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Añadir nuevo gestor")
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Mostrar el diálogo
        dialog.show();

        // Manejar el botón positivo programáticamente para validar antes de cerrar
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();

            if (nombre.isEmpty()) {
                etNombre.setError("El nombre es requerido");
                return;
            }

            // Si pasa las validaciones, agregar el gestor
            addNewGestor(nombre);
            dialog.dismiss();
        });

    }

    private void addNewGestor(String nombre) {
        new Thread(() -> {
            GestorVentas gestor = new GestorVentas(-1, nombre, UniqueCode.createNew());
            UniqueCode code = UniqueCode.createNew();
            dataSource.insertGestor(gestor);

            requireActivity().runOnUiThread(() -> {
                loadGestores(); // Recargar la lista
                Toast.makeText(requireContext(), "Gestor añadido", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataSource.close();
    }
}
