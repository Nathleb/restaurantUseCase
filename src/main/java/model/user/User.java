package model.user;

import model.Named;
import static java.lang.String.format;

public interface User extends Named
{
    String getFirstName();

    String getLastName();

    @Override
    default String getName()
    {
        return format("%s %s", getFirstName(), getLastName().toUpperCase());
    }
}
