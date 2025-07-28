package de.superdupermarkt.shelflife.data.loader.factory;

import java.lang.reflect.InvocationTargetException;

/**
 * Helper class for loading classes dynamically.
 *
 */
public class ClassLoading {

    /**
     * Loads a class dynamically from the given classloader and returns an instance (via default no-args constructor)
     * <p>
     * The classname is the unqualified name (i.e. without package), the actual class will be searched in the package this class resides in.
     * It also ensures that the loaded class is a subclass of a given type.
     *
     * @param classLoader The classloader to use for loading the class. Enables runtime configured classpaths and unloading classes
     * @param className The unqualified name of the class (i.e. String instead of java.lang.String)
     * @param superClass A type of which the loaded class has to be a subclass
     * @return An instance of the given class type
     * @param <T> Type of the class to load
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> T fromClassName(ClassLoader classLoader, String className, Class<T> superClass) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = classLoader.loadClass(ClassLoading.class.getName().replaceFirst("(?<=\\.)[^.]+$", className));
        if (clazz != null && superClass.isAssignableFrom(clazz)) {
            return (T) clazz.getDeclaredConstructor().newInstance();
        }

       throw new ClassCastException("Class '"+ superClass.getName() +"' is not Assignable from '"+ (clazz == null ? "null" : clazz.getName()) +"'");
    }
}
