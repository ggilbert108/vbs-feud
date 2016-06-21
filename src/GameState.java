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
        listeners = new EventListenerList();
        setNumResults(MAX_RESULTS);
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

    private void setNumResults(int n)
    {
        resultText = new String[n];
    }

    private int getNumResults()
    {
        return resultText.length;
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
