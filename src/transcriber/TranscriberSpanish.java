package transcriber;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.decoder.adaptation.Stats;
import edu.cmu.sphinx.decoder.adaptation.Transform;
import edu.cmu.sphinx.result.WordResult;

public class TranscriberSpanish {

	public static void main(String[] args) throws Exception {
		try {
			final String filePath = "Media/diseno_20171002_5950-010110.wav";
			StringBuilder sb = new StringBuilder();

			System.out.println("Loading models...");

			Configuration configuration = new Configuration();

			// Load model from path
			configuration.setAcousticModelPath("ES/acoustic");
			configuration.setDictionaryPath("ES/es.dict");
			configuration.setLanguageModelPath("ES/es-20k.lm");

			StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
			//Audio needs to be .wav, mono channel, 16KHz sample rate, normalized
			//https://audio.online-convert.com/convert-to-wav
			InputStream stream = new FileInputStream(new File(filePath));

			stream.skip(44);

			// Simple recognition with generic model
			recognizer.startRecognition(stream);
			SpeechResult result;
			while ((result = recognizer.getResult()) != null) {

				System.out.format("Hypothesis: %s\n", result.getHypothesis());
				sb.append(result.getHypothesis()).append(" ");

				System.out.println("List of recognized words and their times:");
				for (WordResult r : result.getWords()) {
					System.out.println(r);
				}

				System.out.println("Best 3 hypothesis:");
				for (String s : result.getNbest(3))
					System.out.println(s);
			}
			recognizer.stopRecognition();

			System.out.println(sb.toString());


			// Live adaptation to speaker with speaker profiles

			stream = TranscriberSpanish.class.getResourceAsStream("Media/prueba.wav");
			stream.skip(44);

			// Stats class is used to collect speaker-specific data
			Stats stats = recognizer.createStats(1);
			recognizer.startRecognition(stream);
			while ((result = recognizer.getResult()) != null) {
				stats.collect(result);
			}
			recognizer.stopRecognition();

			// Transform represents the speech profile
			Transform transform = stats.createTransform();
			recognizer.setTransform(transform);

			// Decode again with updated transform
			stream = TranscriberSpanish.class.getResourceAsStream(filePath);
			stream.skip(44);
			recognizer.startRecognition(stream);
			while ((result = recognizer.getResult()) != null) {
				System.out.format("Hypothesis: %s\n", result.getHypothesis());
			}
			recognizer.stopRecognition();

		} catch (Exception e) {
			System.exit(0);
		}

	}
}