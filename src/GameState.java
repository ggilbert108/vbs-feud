import javax.swing.event.EventListenerList;
import java.util.*;

/**
 * Created by gaston on 6/21/2016.
 */
public class GameState
{
    public final static int MAX_RESULTS = 8;
    public final static int MAX_RESULTS_HALF = 4;


    private String[] resultText;
    private int[] responses;
    private EventListenerList listeners = new EventListenerList();


    public GameState()
    {
        setNumResults(0);
        listeners = new EventListenerList();
    }

    public void addWrongAnswerListener(WrongAnswerListener listener) { listeners.add(WrongAnswerListener.class, listener); }

    public void addResetListener(ResetListener listener)
    {
        listeners.add(ResetListener.class, listener);
    }

    public void addResultSubmittedListener(ResultSubmittedListener listener)
    {
        listeners.add(ResultSubmittedListener.class, listener);
    }

    public void submitResultText(int index, String text)
    {
        resultText[index] = text;
        fireResultSubmitted(index);
    }

    private void fireResultSubmitted(int index)
    {
        ResultSubmittedListener[] resultSubmittedListeners = listeners.getListeners(ResultSubmittedListener.class);
        for(ResultSubmittedListener listener : resultSubmittedListeners)
        {
            listener.resultSubmitted(new SubmissionEvent(this, index));
        }
    }

    private void fireReset()
    {
        ResetListener[] resultSubmittedListeners = listeners.getListeners(ResetListener.class);
        for(ResetListener listener : resultSubmittedListeners)
        {
            listener.reset();
        }
    }

    public void fireWrongAnswer(int index)
    {
        WrongAnswerListener[] resultSubmittedListeners = listeners.getListeners(WrongAnswerListener.class);
        for(WrongAnswerListener listener : resultSubmittedListeners)
        {
            listener.wrongAnswer(index);
        }
    }

    public void setNumResults(int n)
    {
        if(n > 6)
        {
            return;
        }

        resultText = new String[n];
        responses = new int[n];
        setNumResponses();

        fireReset();
    }

    private void setNumResponses()
    {
        Random random = new Random();
        ArrayList<Integer> randoms = new ArrayList<>();

        for(int i = 0; i < responses.length - 1; i++)
        {
            randoms.add(random.nextInt(100));
        }
        randoms.add(0);
        randoms.add(100);

        Collections.sort(randoms);

        for(int i = 0; i < responses.length; i++)
        {
            responses[i] = randoms.get(i + 1) - randoms.get(i);
        }

        Arrays.sort(responses);

        for(int i = 0; i < responses.length / 2; i++)
        {
            int backIndex = (responses.length - 1) - i;
            int temp = responses[i];
            responses[i] = responses[backIndex];
            responses[backIndex] = temp;
        }
    }


    public int getNumResults()
    {
        return resultText.length;
    }

    public int getResponses(int index) {return  responses[index];}

    public String getResult(int index)
    {
        return resultText[index];
    }
}

interface ResultSubmittedListener extends EventListener
{
    void resultSubmitted(SubmissionEvent e);
}

class SubmissionEvent extends EventObject
{
    public int index;

    public SubmissionEvent(Object source, int index)
    {
        super(source);
        this.index = index;
    }
}

interface ResetListener extends EventListener
{
    void reset();
}

interface WrongAnswerListener extends EventListener
{
    void wrongAnswer(int index);
}