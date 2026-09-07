package edu.northeastern.NUMAD26Sp_RubenSalido.llevatilde;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.text.Collator;
import java.util.Locale;

public class miLista extends AppCompatActivity {
    public static final String PREFS_NAME = "MiListaPrefs";
    private static final String KEY_PALABRAS_JSON = "palabras_guardadas_json";
    private static final float BUTTON_WIDTH = 220f;

    private RecyclerView recyclerViewMiLista;
    private MiListaAdapter adapter;
    private ArrayList<EntradaLista> palabrasGuardadas;
    private int posicionSwipeActiva = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mi_lista);

        recyclerViewMiLista = findViewById(R.id.recyclerViewMiLista);
        Button buttonSortAZ = findViewById(R.id.buttonSortAZ);
        Button buttonSortZA = findViewById(R.id.buttonSortZA);
        Button buttonSortDate = findViewById(R.id.buttonSortDate);
        Button buttonBorrarTodo = findViewById(R.id.buttonBorrarTodo);
        Button buttonCerrar = findViewById(R.id.buttonCerrar);

        palabrasGuardadas = cargarEntradasDesdePrefs();

        recyclerViewMiLista.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MiListaAdapter(palabrasGuardadas);
        recyclerViewMiLista.setAdapter(adapter);

        // Sort A-Z
        buttonSortAZ.setOnClickListener(view -> {
            cerrarFilaSwipeAbierta();
            Collator collator = Collator.getInstance(new Locale("es", "ES"));
            collator.setStrength(Collator.PRIMARY); // Ignores accent marks and case
            Collections.sort(palabrasGuardadas, (a, b) ->
                    collator.compare(a.getPalabra(), b.getPalabra()));
            adapter.notifyDataSetChanged();
        });

        // Sort Z-A
        buttonSortZA.setOnClickListener(view -> {
            cerrarFilaSwipeAbierta();
            Collator collator = Collator.getInstance(new Locale("es", "ES"));
            collator.setStrength(Collator.PRIMARY); // Ignores accent marks and case
            Collections.sort(palabrasGuardadas, (a, b) ->
                    collator.compare(a.getPalabra(), b.getPalabra()));
            Collections.reverse(palabrasGuardadas);
            adapter.notifyDataSetChanged();
        });

        // Most recent - Más reciente
        buttonSortDate.setOnClickListener(view -> {
            cerrarFilaSwipeAbierta();
            palabrasGuardadas.clear();
            palabrasGuardadas.addAll(cargarEntradasDesdePrefs());
            adapter.notifyDataSetChanged();
        });

        // Delete All - Borrar todo
        buttonBorrarTodo.setOnClickListener(view -> {
            cerrarFilaSwipeAbierta();
            palabrasGuardadas.clear();
            guardarEntradasEnPrefs(palabrasGuardadas);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Lista borrada.", Toast.LENGTH_SHORT).show();
        });

        // Back - Cerrar
        buttonCerrar.setOnClickListener(view -> {
            cerrarFilaSwipeAbierta();
            finish();
        });

        configurarSwipeParaBorrar();
    }

    // Custom swipe now using RecyclerView.OnItemTouchListener.
    // Bypasses ItemTouchHelper, which means no framework animation overrides or interferes.
    private void configurarSwipeParaBorrar() {
        recyclerViewMiLista.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            // In case the user gets sloppy and accidentally swipes, this will require a min.
            // distance to make the gesture count as a swipe.
            private final int touchSlop = ViewConfiguration.get(miLista.this).getScaledTouchSlop();
            private VelocityTracker velocityTracker = null;
            private View trackedView = null;
            // The row view that the finger is touching.
            private int trackedPosition = RecyclerView.NO_POSITION;
            // Finger-down coordinates.
            private float downX = 0f;
            private float downY = 0f;
            // Translation at the moment the finger touched down, to make
            // dragging from an already open row easier.
            private float startTranslationX = 0f;
            // Once the touch is determined to be a hosizontal swipe, this kicks in.
            private boolean swipeConfirmed = false;
            // But if the touch turns out to be a vertical swipe, then it's released.
            private boolean scrollConfirmed = false;
            private boolean borrarTapped = false;

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                handleTouch(rv, e);
                // Returns true once a horizontal swipe is confirmed,
                // ensures vertical scrolling works normally.
                return swipeConfirmed || borrarTapped;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                handleTouch(rv, e);
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {
            }

            private void handleTouch(RecyclerView rv, MotionEvent e) {
                switch (e.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: {
                        swipeConfirmed = false;
                        scrollConfirmed = false;
                        borrarTapped = false;

                        downX = e.getX();
                        downY = e.getY();

                        int[] rvOnScreen = new int[2];
                        rv.getLocationOnScreen(rvOnScreen);
                        float tapX = e.getRawX() - rvOnScreen[0];
                        float tapY = e.getRawY() - rvOnScreen[1];

                        if (posicionSwipeActiva >= 0) {
                            RecyclerView.ViewHolder vh =
                                    rv.findViewHolderForAdapterPosition(posicionSwipeActiva);
                            if (vh != null) {
                                View openView = vh.itemView;
                                RectF borrarRect = new RectF(
                                        openView.getRight() - BUTTON_WIDTH,
                                        openView.getTop(),
                                        openView.getRight(),
                                        openView.getBottom()
                                );
                                if (borrarRect.contains(tapX, tapY)) {
                                    borrarTapped = true;
                                    return;
                                }
                            }
                            cerrarFilaSwipeAbierta();
                        }

                        trackedView = rv.findChildViewUnder(downX, downY);
                        if (trackedView != null) {
                            trackedPosition = rv.getChildAdapterPosition(trackedView);
                            startTranslationX = trackedView.getTranslationX();
                        } else {
                            trackedPosition = RecyclerView.NO_POSITION;
                            startTranslationX = 0f;
                        }

                        if (velocityTracker == null) {
                            velocityTracker = VelocityTracker.obtain();
                        } else {
                            velocityTracker.clear();
                        }
                        velocityTracker.addMovement(e);
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        if (borrarTapped || trackedView == null || scrollConfirmed) break;

                        velocityTracker.addMovement(e);
                        float dx = e.getX() - downX;
                        float dy = e.getY() - downY;

                        if (!swipeConfirmed) {
                            if (Math.abs(dx) > touchSlop && Math.abs(dx) > Math.abs(dy)) {
                                // If it's horizontal, then implement
                                swipeConfirmed = true;
                                // Tell the RecyclerView to not scroll
                                rv.getParent().requestDisallowInterceptTouchEvent(true);
                            } else if (Math.abs(dy) > touchSlop) {
                                // Vertical scroll = let RecyclerView take over
                                // after this closes any open row
                                scrollConfirmed = true;
                                cerrarFilaSwipeAbierta();
                                break;
                            } else {
                                break;
                            }
                        }

                        //
                        float newTx = startTranslationX + dx;
                        newTx = Math.max(-BUTTON_WIDTH, Math.min(0f, newTx));
                        trackedView.setTranslationX(newTx);
                        rv.invalidateItemDecorations();
                        rv.invalidate();
                        break;
                    }

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        if (borrarTapped) {
                            if (e.getActionMasked() == MotionEvent.ACTION_UP) {
                                int posToDelete = posicionSwipeActiva;
                                RecyclerView.ViewHolder vh =
                                        rv.findViewHolderForAdapterPosition(posToDelete);
                                if (vh != null) vh.itemView.setTranslationX(0f);
                                posicionSwipeActiva = -1;
                                eliminarPalabra(posToDelete);
                            }
                            borrarTapped = false;
                            resetTouchState();
                            break;
                        }

                        if (trackedView == null) break;

                        if (!swipeConfirmed) {
                            // Turns out to be a tap, and not a swipe
                            resetTouchState();
                            break;
                        }

                        velocityTracker.addMovement(e);
                        velocityTracker.computeCurrentVelocity(1000);
                        float vx = velocityTracker.getXVelocity();
                        float currentTx = trackedView.getTranslationX();

                        // Decides whether snap open or snap closed
                        // open = snap to -BUTTON_WIDTH
                        // Closed = snap to 0
                        boolean snapOpen;
                        if (Math.abs(vx) > 800) {
                            // Fast fling decides direction
                            snapOpen = vx < 0;
                        } else {
                            // Crossed the halfway threshold
                            snapOpen = currentTx < -(BUTTON_WIDTH / 2f);
                        }

                        if (snapOpen) {
                            snapRowOpen(trackedView, trackedPosition);
                        } else {
                            snapRowClosed(trackedView, trackedPosition);
                        }

                        resetTouchState();
                        break;
                    }
                }
            }

            private void resetTouchState() {
                swipeConfirmed = false;
                scrollConfirmed = false;
                borrarTapped = false;
                trackedView = null;
                trackedPosition = RecyclerView.NO_POSITION;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
            }
        });

        // Draws the red 'Borrar' background behind open rows using an
        // ItemDecoration, this makes it completely independent of ItemTouchHelper
        recyclerViewMiLista.addItemDecoration(new RecyclerView.ItemDecoration() {
            private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            @Override
            public void onDraw(@NonNull Canvas c,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {

                int count = parent.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = parent.getChildAt(i);
                    int pos = parent.getChildAdapterPosition(child);

                    // Only draws if the row is opened - swiped left
                    if (child.getTranslationX() < 0) {
                        RectF borrarRect = new RectF(
                                child.getRight() - BUTTON_WIDTH,
                                child.getTop(),
                                child.getRight(),
                                child.getBottom()
                        );

                        // Red background
                        paint.setColor(Color.RED);
                        c.drawRect(borrarRect, paint);

                        // 'Borrar' label
                        paint.setColor(Color.WHITE);
                        paint.setTextSize(36f);
                        dibujarTextoCentrado(c, paint, "Borrar", borrarRect);
                    }
                }
            }
        });
    }

    // Animates a row smoothly to the open position and tracks it
    private void snapRowOpen(View itemView, int position) {
        if (posicionSwipeActiva >= 0 && posicionSwipeActiva != position) {
            RecyclerView.ViewHolder oldHolder =
                    recyclerViewMiLista.findViewHolderForAdapterPosition(posicionSwipeActiva);
            if (oldHolder != null) {
                oldHolder.itemView.animate()
                        .translationX(0f)
                        .setDuration(150)
                        .withEndAction(() -> {
                            recyclerViewMiLista.invalidateItemDecorations();
                            recyclerViewMiLista.invalidate();
                        })
                        .start();
            }
        }

        posicionSwipeActiva = position;
        itemView.animate()
                .translationX(-BUTTON_WIDTH)
                .setDuration(150)
                .withEndAction(() -> {
                    recyclerViewMiLista.invalidateItemDecorations();
                    recyclerViewMiLista.invalidate();
                })
                .start();
    }

    // Animates a row smoothly back to closed position
    private void snapRowClosed(View itemView, int position) {
        if (posicionSwipeActiva == position) {
            posicionSwipeActiva = -1;
        }
        itemView.animate()
                .translationX(0f)
                .setDuration(150)
                .withEndAction(() -> {
                    recyclerViewMiLista.invalidateItemDecorations();
                    recyclerViewMiLista.invalidate();
                })
                .start();
    }

    // If any row is open, it closes it with animation
    private void cerrarFilaSwipeAbierta() {
        if (posicionSwipeActiva >= 0) {
            RecyclerView.ViewHolder holder =
                    recyclerViewMiLista.findViewHolderForAdapterPosition(posicionSwipeActiva);
            if (holder != null) {
                holder.itemView.animate()
                        .translationX(0f)
                        .setDuration(150)
                        .withEndAction(() -> {
                            recyclerViewMiLista.invalidateItemDecorations();
                            recyclerViewMiLista.invalidate();
                        })
                        .start();
            }
            posicionSwipeActiva = -1;
        }
    }

    // ----------------------------------------------------------
    // SharedPreferences helpers
    // ----------------------------------------------------------

    private ArrayList<EntradaLista> cargarEntradasDesdePrefs() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_PALABRAS_JSON, "[]");

        ArrayList<EntradaLista> lista = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String palabra = obj.getString("palabra");
                String tipo = obj.getString("tipo");
                lista.add(new EntradaLista(palabra, tipo));
            }
        } catch (JSONException e) {
            android.util.Log.e("miLista", "Error al cargar palabras desde SharedPreferences", e);
        }

        return lista;
    }


    private void guardarEntradasEnPrefs(ArrayList<EntradaLista> lista) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        for (EntradaLista entrada : lista) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("palabra", entrada.getPalabra());
                obj.put("tipo", entrada.getTipo());
            } catch (JSONException e) {
                android.util.Log.e("miLista", "Error al guardar entrada", e);
            }
            jsonArray.put(obj);
        }
        prefs.edit().putString(KEY_PALABRAS_JSON, jsonArray.toString()).apply();
    }

    // ----------------------------------------------------------
    // Drawing helper
    // ----------------------------------------------------------

    private void dibujarTextoCentrado(Canvas canvas, Paint paint, String texto, RectF rectF) {
        float textWidth = paint.measureText(texto);
        float x = rectF.centerX() - (textWidth / 2f);
        Paint.FontMetrics fm = paint.getFontMetrics();
        float y = rectF.centerY() - ((fm.ascent + fm.descent) / 2f);
        canvas.drawText(texto, x, y, paint);
    }

    // ----------------------------------------------------------
    // Delete helper
    // ----------------------------------------------------------

    private void eliminarPalabra(int position) {
        if (position >= 0 && position < palabrasGuardadas.size()) {
            EntradaLista entrada = palabrasGuardadas.remove(position);
            guardarEntradasEnPrefs(palabrasGuardadas);
            adapter.notifyItemRemoved(position);
            Toast.makeText(
                    this,
                    entrada.getPalabra() + " ha sido borrada de tu lista.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
