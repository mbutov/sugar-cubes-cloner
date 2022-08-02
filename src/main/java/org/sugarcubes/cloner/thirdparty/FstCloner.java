package org.sugarcubes.cloner.thirdparty;

import org.nustaq.serialization.simpleapi.DefaultCoder;
import org.nustaq.serialization.simpleapi.FSTCoder;
import org.sugarcubes.cloner.AbstractCloner;

/**
 * The implementation of {@link org.sugarcubes.cloner.Cloner} which uses FST serialization for cloning.
 *
 * @see <a href="https://github.com/RuedigerMoeller/fast-serialization">fast-serialization</a>
 *
 * @author Maxim Butov
 */
public class FstCloner extends AbstractCloner {

    /**
     * FST coder.
     */
    private final FSTCoder coder;

    /**
     * Constructor.
     */
    public FstCloner() {
        this(new DefaultCoder());
    }

    /**
     * Constructor.
     */
    public FstCloner(FSTCoder coder) {
        this.coder = coder;
    }

    @Override
    protected Object doClone(Object object) throws Throwable {
        return coder.toObject(coder.toByteArray(object));
    }

}
