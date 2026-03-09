package mapsaroundyou.storage;

import java.io.IOException;
import java.io.Reader;

@FunctionalInterface
interface ReaderSupplier {
    Reader open() throws IOException;
}
