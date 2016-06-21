import javax.swing.event.EventListenerList;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Created by gaston on 6/21/2016.
 */
public class GameState
{
    public final static int MAX_RESULTS = 8;
    public final static int MAX_RESULTS_HALF = 4;


    private String[] resultText;
    private EventListenerList listeners = new EventListenerList();

    public GameState()
    {
        setNumResults(0);
        listeners = new EventListenerList();
    }

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

    public void setNumResults(int n)
    {
        if(n > 6)
        {
            return;
        }

        resultText = new String[n];
        fireReset();
    }



    public int getNumResults()
    {
        return resultText.length;
    }

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
