import java.text.SimpleDateFormat;
import java.util.Date;

public class Voter
{
    private String name;
    private Date birthDay;
    private int voteCounts; // добавлен счетчик голосов

    public Voter(String name, Date birthDay)
    {
        this.name = name;
        this.birthDay = birthDay;
        this.voteCounts = 0;
    }

    @Override
    public boolean equals(Object obj)
    {
        Voter voter = (Voter) obj;
        return name.equals(voter.name) && birthDay.equals(voter.birthDay);
    }

    @Override
    public int hashCode()
    {
        long code = name.hashCode() + birthDay.hashCode();
        while(code > Integer.MAX_VALUE) {
            code = code/10;
        }
        return (int) code;
    }

    public String toString()
    {
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
        return name + " (" + dayFormat.format(birthDay) + ")";
    }

    public String getName()
    {
        return name;
    }

    public Date getBirthDay()
    {
        return birthDay;
    }

    public int getVoteCounts() {
        return voteCounts;
    }

    public void setVoteCounts(int voteCounts) {
        this.voteCounts = voteCounts;
    }
}
