package todolist.cursoandroid.com.todolist;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText textoTarefa;
    private Button botaoAdicionar;
    private ListView listaTarefas;
    private SQLiteDatabase bd;
    private ArrayAdapter<String> itemAdapter;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            //Recuperar componentes
            textoTarefa = (EditText) findViewById(R.id.editTextId);
            botaoAdicionar = (Button) findViewById(R.id.buttonAdicionarId);

            //lista
            listaTarefas = (ListView) findViewById(R.id.listViewId);

            //Banco de Dados
            bd = openOrCreateDatabase("apptarefas", MODE_PRIVATE, null);

            //Criar tabelas
            bd.execSQL("CREATE TABLE IF NOT EXISTS tarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            botaoAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String textoDigitado = textoTarefa.getText().toString();
                    salvarTarefa(textoDigitado);
                }
            });

            listaTarefas.setLongClickable(true);
            listaTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    removerTarefa(ids.get(position));
                    return true;
                }
            });

            recuperarTarefas();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void salvarTarefa(String texto) {
        try {
            if (texto.equals("")) {
                Toast.makeText(MainActivity.this, "Digite uma terefa", Toast.LENGTH_LONG).show();
            } else {
                bd.execSQL("INSERT INTO tarefas (tarefa) VALUES ('" + texto + "')");
                Toast.makeText(MainActivity.this, "Tarefa salva com sucesso", Toast.LENGTH_LONG).show();
                recuperarTarefas();
                textoTarefa.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recuperarTarefas() {
        try {
            //Recuperar tarefas
            Cursor cursor = bd.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            //Recuperar os ids das colunas
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            //Criar adaptador
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itemAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text2,
                    itens);
            listaTarefas.setAdapter(itemAdapter);

            //listas as tarefas
            cursor.moveToFirst();
            while (cursor != null) {

                Log.i("Resultado - ", "tarefa: " + cursor.getString(indiceColunaTarefa));
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
                cursor.moveToNext();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removerTarefa(Integer id) {
        try {

            bd.execSQL("DELETE FROM tarefas WHERE id = " + id);
            Toast.makeText(MainActivity.this, "Tarefa removida com sucesso", Toast.LENGTH_SHORT).show();
            recuperarTarefas();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
