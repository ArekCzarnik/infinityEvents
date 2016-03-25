package de.infinity.events.utils;

import com.esotericsoftware.kryo.Kryo;
import de.infinity.events.domain.PatchEvent;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class KryoUtils {

    public static final ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<Kryo>() {

        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.register(PatchEvent.class);
            kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            return kryo;
        }
    };
}
