package transcriber;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;

public class SimpleTranscriberSpanish {       

	public static void main(String[] args) throws Exception {

		final String filePath = "Media/diseno_20171002_5950-010110.wav";
		StringBuilder sb = new StringBuilder();

		Configuration configuration = new Configuration();

		configuration.setAcousticModelPath("ES/acoustic");
		configuration.setDictionaryPath("ES/es.dict");
		configuration.setLanguageModelPath("ES/es-20k.lm");
		configuration.setSampleRate(44100);


		StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
		InputStream stream = new FileInputStream(new File(filePath));

		recognizer.startRecognition(stream);
		SpeechResult result;
		while ((result = recognizer.getResult()) != null) {
			System.out.format("Hypothesis: %s\n", result.getHypothesis());
			sb.append(result.getHypothesis()).append(" ");
		}
		System.out.println(sb.toString());

		recognizer.stopRecognition();
	}
}