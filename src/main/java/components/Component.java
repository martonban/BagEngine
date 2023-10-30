package components;

import engine.GameObject;
import imgui.ImGui;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/*
* This is an abstract class. Every GameObject can have multiple Component.
* These GameObject need to have a common abstract class
* The ImGui part is exposing data to the editor through Fields
* */


public abstract class Component {

    private static int ID_COUNTER = 0;
    private int uid = -1;

    public transient GameObject gameObject = null;

    public void start() {

    }

    public void update(float dt) {

    };

    public void imgui() {
        try{
            // Get all Declared data from the subclass e.g SpriteRenderer
            Field[] fields = this.getClass().getDeclaredFields();

            // Go throw all of them Fields ang get the class(or type of the data), the name of the field and the value of it
            // After we find the type, based on it, we'll set the imgui.
            // And we can change the values throw imgui
            for( Field field : fields) {

                // Identify private or transient data
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if(isTransient){
                    continue;
                }

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate) {
                    field.setAccessible(true);
                }
                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                // Go threw all possible data type, BASED ON OUR CODE!!!
                if(type == int.class) {
                    int val = (int)value;
                    int[] imInt = {val};
                    if (ImGui.dragInt(name + ": ", imInt)) {
                       field.set(this, imInt[0]);
                    }
                } else if (type == float.class) {
                    float val = (float)value;
                    float[] imFloat = {val};
                    if(ImGui.dragFloat(name + ": ", imFloat)){
                        field.set(this, imFloat[0]);
                    }
                } else if (type == boolean.class) {
                    boolean val = (boolean)value;
                    boolean[] imBool = {val};
                    if(ImGui.checkbox(name + ": ", val)) {
                        val = !val;
                        field.set(this, !val);
                    }
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec = {val.x, val.y, val.z};
                    if(ImGui.dragFloat3(name + ": ", imVec)){
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if(ImGui.dragFloat4(name + ": ", imVec)){
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                    }
                }

                if (isPrivate) {
                    field.setAccessible(false);
                }

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void generateID() {
        if(this.uid == -1) {
            this.uid = ID_COUNTER++;
        }
    }

    public int getUid() {
        return this.uid;
    }

    public static void init(int maxID) {
        ID_COUNTER = maxID;
    }
}
