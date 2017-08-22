package de.infinity.events.utils;

import com.esotericsoftware.kryo.Kryo;
import de.infinity.events.domain.CaretEvent;
import de.infinity.events.domain.CreateFile;
import de.infinity.events.domain.PatchEvent;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class KryoUtils {

    public static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(PatchEvent.class);
        kryo.register(CaretEvent.class);
        kryo.register(CreateFile.class);
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        return kryo;
    });
}
