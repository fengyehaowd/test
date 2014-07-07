

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringBufferInputStream;
import java.nio.CharBuffer;
import java.security.AllPermission;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import cn.ac.trimps.processor.HashCalc;

/**
 * ����.http��.hdr�ļ���ת������
 * 
 * @author wyj
 * 
 */
public class batch_fenghuo_processor implements Runnable {
	public String worker_name = null;
	public File[] input_file_list = null;
	public boolean whether_parse_hdr = false;

	public batch_fenghuo_processor(String name, File[] files,
			boolean whether_parse_hdr) {
		this.worker_name = name;
		this.input_file_list = files;
		this.whether_parse_hdr = whether_parse_hdr;
	}

	/**
	 * ����Ϣ�����ָ���������
	 * 
	 * @param writer
	 * @param message
	 */
	static public void log(BufferedWriter writer, String message) {
		try {
			writer.write(message);
			writer.newLine();
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		BufferedWriter console = null, error = null, output = null;
		try {
			console = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(this.worker_name + "-console")));
			error = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(this.worker_name + "-error")));
		} catch (Exception e) {
			throw new RuntimeException();
		}

		/** ���δ������Ŀ¼�� */
		for (File input : input_file_list) {
			/** 1.�ж��ļ��洢Ŀ¼�Ƿ����� */
			if (!input.exists() || !input.isDirectory() || !input.canRead()) {
				throw new RuntimeException(
						"some exceptions happened when deals with "
								+ input.getAbsolutePath()
								+ ", pls check it's isDirectory?, exists? or canRead?");
			}
			try {
				output = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(input.getName() + "-output")));
			} catch (FileNotFoundException e) {
				log(error, "open output file for " + input.getAbsolutePath()
						+ " failed, ignore this dir");
				continue;
			}

			File[] web_site_list = input.listFiles();
			try {
				/** ���α���input��������web_site�ļ� */
				for (File web_site : web_site_list) {
					long begin = System.currentTimeMillis();
					if (!web_site.exists() || !web_site.isDirectory()
							|| !web_site.canRead()) {
						log(error,
								"pls check whether "
										+ web_site.getAbsolutePath()
										+ " exists? || isDirectory? || canRead?");
						continue;
					}
					/** 2.���α��������б������� */

					/** ��ȡinputĿ¼��,������.http��ʽ��β���ļ��б� */
					String[] filename_list = web_site
							.list(new FilenameFilter() {
								@Override
								public boolean accept(File dir, String name) {
									if (name.endsWith(".http")) {
										return true;
									} else {
										return false;
									}
								}
							});
					for (String filename : filename_list) {
						try {
							//process_single_http_post(web_site, filename,
							//		whether_parse_hdr, output, console, error);
						} catch (Exception e) {
							log(error, "process " + filename
									+ " error, exception is " + e.getMessage());
							continue;
						}
					}

					long end = System.currentTimeMillis();
					log(console, "process " + web_site.getAbsolutePath()
							+ " cost " + (end - begin) + " ms");
				}
			} finally {
				if (output != null) {
					try {
						output.close();
					} catch (Exception e) {
					}
				}
			}
		}

		try {
			if (console != null) {
				console.flush();
				console.close();
			}
			if (error != null) {
				error.flush();
				error.close();
			}
		} catch (IOException e) {
		}
	}

	/**
	 * �����ػ��һ��http_post��������,�������ļ����,�ֱ�����:
	 * <P>
	 * 2399285a-3e0d-11e2-c000-0015177a6e0d-2481159056-20434.http(�洢����http
	 * post����)
	 * </P>
	 * <P>
	 * 2399285a-3e0d-11e2-c000-0015177a6e0d-2481159056-20434.http.hdr(
	 * �洢��Ӧ��TCPͷ����Ϣ��)
	 * </P>
	 * 
	 * Ŀǰ֧������hash���㷽ʽ:
	 * <P>
	 * public static final int script_url_body = 0;
	 * </P>
	 * <P>
	 * public static final int url_body = 1;
	 * </P>
	 * <P>
	 * public static final int url_cookie_body = 2;
	 * </P>
	 * 
	 * @param dir
	 *            �洢.http��.http.hdr�ļ��Ĺ���Ŀ¼
	 * @param http_post_name
	 *            ��Ҫ���������.http�ļ�
	 * @param whether_parse_hdr
	 *            �Ƿ���Ҫ����.hdr�ļ�
	 * @param output
	 *            ����<host,script,hash1,hash2,hash3>д�����յĽ���ļ�
	 * @param console
	 *            ���𽫲��������еı�׼���д�뵽����ļ�
	 * @param error
	 *            ���𽫲��������е��쳣���д�뵽����ļ�
	 * @throws IOException
	 */
//	public void process_single_http_post(File dir, String http_post_name,
//			boolean whether_parse_hdr, BufferedWriter output,
//			BufferedWriter console, BufferedWriter error) throws IOException {
//		/** 1.��http post��Ӧ�ļ�,��������Ӧ�ж� */
//		BufferedReader header_reader = null, protocol_reader = null;
//		if (whether_parse_hdr) {
//			File tcp_header = new File(dir, http_post_name + ".hdr");
//			if (!tcp_header.exists()) {
//				throw new RuntimeException(tcp_header.getAbsolutePath()
//						+ " doesn't exists");
//			}
//			header_reader = new BufferedReader(new InputStreamReader(
//					new FileInputStream(tcp_header)));
//		}
//
//		File http_protocol = new File(dir, http_post_name);
//		if (!http_protocol.exists()) {
//			throw new RuntimeException(http_protocol.getAbsolutePath()
//					+ " doesn't exists");
//		}
//		protocol_reader = new BufferedReader(new InputStreamReader(
//				new FileInputStream(http_protocol)));
//
//		try {
//			if (whether_parse_hdr) {
//				/** �����Ҫ����.hdr�ļ�,����øú��� */
//				process_header(header_reader, output, console, error);
//			}
//			/**
//			 * process_protocol_body�������س���Ϊ6������,����Ϊ
//			 * <P>
//			 * host:string
//			 * </P>
//			 * <P>
//			 * script_name:string
//			 * </P>
//			 * <P>
//			 * url_key_set:string<String>
//			 * </P>
//			 * <P>
//			 * cookie_key_set:string<String>
//			 * </P>
//			 * <P>
//			 * body_key_set:string<String>
//			 * </P>
//			 * <P>
//			 * attachment_file_name:string<String>
//			 * </P>
//			 */
//			String[] result = process_protocol_body(dir, http_post_name,
//					protocol_reader, console, error);
//			String host = result[0];
//			String script = result[1];
//			String url_key_set = result[2];
//			String cookie_key_set = result[3];
//			String body_key_set = result[4];
//			String attachment_file_name = result[5];
//			/**
//			 * </P> Ŀǰ֧������hash���㷽ʽ:
//			 * <P>
//			 * public static final int script_url_body = 0;
//			 * </P>
//			 * <P>
//			 * public static final int url_body = 1;
//			 * </P>
//			 * <P>
//			 * public static final int url_cookie_body = 2;
//			 * </P>
//			 */
//			String hash1 = HashCalc.digest(new String[] { script, url_key_set,
//					body_key_set });
//			String hash2 = HashCalc.digest(new String[] { url_key_set,
//					body_key_set });
//			String hash3 = HashCalc.digest(new String[] { url_key_set,
//					cookie_key_set, body_key_set });
//
//			output.write((host == null ? ' ' : host) + "\t"
//					+ (script == null ? ' ' : script) + "\t" + hash1 + "\t"
//					+ hash2 + "\t" + hash3);
//			output.newLine();
//			output.flush();
//		} catch (NoSuchAlgorithmException e) {
//			log(error, e.getMessage());
//		} finally {
//			if (header_reader != null) {
//				header_reader.close();
//			}
//			if (protocol_reader != null) {
//				protocol_reader.close();
//			}
//		}
//	}

	/**
	 * ����.hdr�ļ�
	 * 
	 * @param header_reader
	 *            ��ȡ.hdr�ļ�ʹ�õ�BufferedReader
	 * @param output
	 *            ��������д�뵽�������ļ�
	 * @param console
	 *            ����־д�����ն�ʹ�õ�BufferedWriter
	 * @param error
	 *            ��������Ϣд�����ն�ʹ�õ�BufferedWriter
	 * @throws IOException
	 */
	public void process_header(BufferedReader header_reader,
			BufferedWriter output, BufferedWriter console, BufferedWriter error)
			throws IOException {
		String line = null;
		for (; (line = header_reader.readLine()) != null;) {
			if (line.trim().length() == 0) {
				continue;
			}
			int first_index = line.indexOf(':');
			String key = line.substring(0, first_index).trim();
			String sub_line = line.substring(first_index + 1).trim();
			int second_index = sub_line.indexOf(';');
			if (second_index != -1) {
				String value = sub_line.substring(0, second_index);
				System.out.println(key + " = " + value);
			} else {
				String value = sub_line.trim();
				System.out.println(key + " = " + value);
			}
		}
	}

	/**
	 * �ӱ��ĵ�����������ȡ�ű�������Ϣ,��Ҫ�������¼��������Ϣ:
	 * <P>
	 * <request line>��url��ͨ��?name1=value1&...&nameN=valueN��ʽЯ���ı���
	 * </P>
	 * <P>
	 * <headers/cookie>����A1=B1;...;AN=BN��ʽЯ���ı�����
	 * </P>
	 * <P>
	 * <request-body>����name1=value1&name2=value2&...&nameN=valueN��ʽЯ���ı���
	 * </P>
	 * <P>
	 * ������
	 * </P>
	 * 
	 * TODO��1,��һ��http��׼�淶�и��������ַ�ʽ; 2,��һ��cookie��Я�������ķ�ʽ
	 * 
	 * @param dir
	 *            �洢http_post_file_name�ļ��Ĺ���Ŀ¼
	 * @param http_post_file_name
	 *            �ȴ������http_post�ļ�����
	 * @param reader
	 *            ��ȡ.http�ļ�ʹ�õ�Reader����
	 * @param console
	 * @param error
	 * @return ���س���Ϊ6������,����Ϊ
	 *         <P>
	 *         host:string
	 *         </P>
	 *         <P>
	 *         script_name:string
	 *         </P>
	 *         <P>
	 *         url_key_set:List<String>
	 *         </P>
	 *         <P>
	 *         cookie_key_set:List<String>
	 *         </P>
	 *         <P>
	 *         body_key_set:List<String>
	 *         </P>
	 *         <P>
	 *         attachment_file_name:List<String>
	 *         </P>
	 * @throws IOException
	 */
	static public String[] process_protocol_body(String Url,String http_post)
			throws IOException {
		String host = null, script = null, content_type = null, url_key_set = null, cookie_key_set = null, body_key_set = null, attachment_file_name = null;
		/**
		 * ����http_post��������,�������ݰ��������ĸ�����,��Ҫ�ֱ������ȡ:
		 * <P>
		 * <request line>
		 * </P>
		 * <P>
		 * <headers>
		 * </P>
		 * <P>
		 * <blank line>
		 * </P>
		 * <P>
		 * <request-body>
		 * </P>�����ļ�����¼������Ϊ�ǲ���ġ�
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(new StringBufferInputStream(http_post)));
		BufferedWriter console = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:\\console")));
		BufferedWriter error = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:\\error")));
		boolean after_request_line = false, after_header = false, content_type_detect = false, is_supported_content_type = true;
		String line = null;
		StringBuilder http_post_body_builder = new StringBuilder();
		/** ���ζ�ȡhttp_post���ֵ����� */
		for (; (line = reader.readLine()) != null;) {
			if (!after_request_line) {
				try {
					String[] temp_result = process_request_line(line);
					script = temp_result[0];
					url_key_set = temp_result[1];
					after_request_line = true;
					continue;
				} catch (Exception e) {
					log(error, "processing " + Url
							+ "'s request_line failed, and request_line is "
							+ line);
					break;
				}
			}

			if (line.trim().length() != 0 && !after_header) {
				/** ���ڴ���<headers>���� */
				int index = line.trim().indexOf(':');
				String key = null;
				if (index != -1) {
					key = line.substring(0, index);
				} else {
					System.err.println("error in processing header field");
				}
				if (key.trim().equalsIgnoreCase("Host")) {
					host = process_host_tag(line);
					continue;
				} else if (key.trim().equalsIgnoreCase("Cookie")) {
					cookie_key_set = process_cookie_tag(line);
					continue;
				} else if (key.trim().equalsIgnoreCase("Content-Type")) {
					content_type_detect = true;
					content_type = process_content_type(line);
					log(console, content_type);
					/** Ŀǰֻ����content_type�а���text���ݵ�http_post��Ϣ,�������Թ� */
					if (!content_type
							.equalsIgnoreCase("application/x-www-form-urlencoded")
							&& !content_type.contains("text")) {
						log(console, "can't process " + Url+ ", it's content-type is " + content_type);
						is_supported_content_type = false;
						continue;
					}
					if (content_type
							.equalsIgnoreCase("multipart/form-data; boundary=")) {
						System.out.println("multipart/form-data in "
								+ Url);
						continue;
					}
				}
			}
			if (line.trim().length() == 0) {
				/** blank line,���� */
				after_header = true;
				if (!content_type_detect) {
					log(console,
							"http_post header process finished, but no content_type detect");
				}
				continue;
			}

			/** ���headers�����Ѿ��������,ͬʱ,http_post��content_typeΪ֧�ֵ�����,����Ӧ��line��� */
			if (after_header && line.length() > 0 && is_supported_content_type) {
				http_post_body_builder.append(line.toString()).append("&");
			}
		}

		/** ���ܴ���http_post_body����Ϊnull�����,���������,http_post_body_builder�ĳ���Ӧ��С��1 */
		if (http_post_body_builder.length() > 1) {
			http_post_body_builder
					.deleteCharAt(http_post_body_builder.length() - 1);
			/** ����body���� */
			body_key_set = process_http_body(http_post_body_builder.toString());
		}

		String[] result = { host, script, url_key_set, cookie_key_set,
				body_key_set, attachment_file_name };
		return result;
	}
	
	static public String[] process_protocol_body(String Url,BufferedReader reader)
	throws IOException {
String host = null, script = null, content_type = null, url_key_set = null, cookie_key_set = null, body_key_set = null, attachment_file_name = null;
/**
 * ����http_post��������,�������ݰ��������ĸ�����,��Ҫ�ֱ������ȡ:
 * <P>
 * <request line>
 * </P>
 * <P>
 * <headers>
 * </P>
 * <P>
 * <blank line>
 * </P>
 * <P>
 * <request-body>
 * </P>�����ļ�����¼������Ϊ�ǲ���ġ�
 */
//BufferedReader reader = new BufferedReader(new InputStreamReader(new StringBufferInputStream(http_post)));
BufferedWriter console = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:\\console")));
BufferedWriter error = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:\\error")));
boolean after_request_line = false, after_header = false, content_type_detect = false, is_supported_content_type = true;
String line = null;
StringBuilder http_post_body_builder = new StringBuilder();
/** ���ζ�ȡhttp_post���ֵ����� */
for (; (line = reader.readLine()) != null;) {
	if (!after_request_line) {
		try {
			String[] temp_result = process_request_line(line);
			script = temp_result[0];
			url_key_set = temp_result[1];
			after_request_line = true;
			continue;
		} catch (Exception e) {
			log(error, "processing " + Url
					+ "'s request_line failed, and request_line is "
					+ line);
			break;
		}
	}

	if (line.trim().length() != 0 && !after_header) {
		/** ���ڴ���<headers>���� */
		int index = line.trim().indexOf(':');
		String key = null;
		if (index != -1) {
			key = line.substring(0, index);
		} else {
			System.err.println("error in processing header field");
		}
		if (key.trim().equalsIgnoreCase("Host")) {
			host = process_host_tag(line);
			continue;
		} else if (key.trim().equalsIgnoreCase("Cookie")) {
			cookie_key_set = process_cookie_tag(line);
			continue;
		} else if (key.trim().equalsIgnoreCase("Content-Type")) {
			content_type_detect = true;
			content_type = process_content_type(line);
			log(console, content_type);
			/** Ŀǰֻ����content_type�а���text���ݵ�http_post��Ϣ,�������Թ� */
			if (!content_type
					.equalsIgnoreCase("application/x-www-form-urlencoded")
					&& !content_type.contains("text")) {
				log(console, "can't process " + Url+ ", it's content-type is " + content_type);
				is_supported_content_type = false;
				continue;
			}
			if (content_type
					.equalsIgnoreCase("multipart/form-data; boundary=")) {
				System.out.println("multipart/form-data in "
						+ Url);
				continue;
			}
		}
	}
	if (line.trim().length() == 0) {
		/** blank line,���� */
		after_header = true;
		if (!content_type_detect) {
			log(console,
					"http_post header process finished, but no content_type detect");
		}
		continue;
	}

	/** ���headers�����Ѿ��������,ͬʱ,http_post��content_typeΪ֧�ֵ�����,����Ӧ��line��� */
	if (after_header && line.length() > 0 && is_supported_content_type) {
		http_post_body_builder.append(line.toString()).append("&");
	}
}

/** ���ܴ���http_post_body����Ϊnull�����,���������,http_post_body_builder�ĳ���Ӧ��С��1 */
if (http_post_body_builder.length() > 1) {
	http_post_body_builder
			.deleteCharAt(http_post_body_builder.length() - 1);
	/** ����body���� */
	body_key_set = process_http_body(http_post_body_builder.toString());
}

String[] result = { host, script, url_key_set, cookie_key_set,
		body_key_set, attachment_file_name };
return result;
}

	/**
	 * ��request_line��URL������ȡ�����б�,�鿴�Ƿ�����?name1=value1&...&name_N=value_N
	 * 
	 * @param request_line
	 */
	static public String[] process_request_line(String request_line) {
		StringTokenizer tokenizer = new StringTokenizer(request_line, " ",
				false);
		/** ����action[POST|GET] */
		tokenizer.nextToken();
		String full_url = tokenizer.nextToken();
		/** ����HTTP/1.1 VERSION */
		tokenizer.nextToken();

		String script_name = null;
		StringBuilder url_key_set = new StringBuilder();
		/** ��ȡ������ʼ�±� */
		int index = full_url.indexOf('?');
		if (index != -1) {
			/** ���ڲ����б� */
			String url_key_values = full_url.substring(index + 1);

			/** ����host����'/'��'?'֮������,��ȡ��Ч��script_name */
			String script_name_temp = full_url.substring(0, index);
			int sub_index = script_name_temp.lastIndexOf('/');
			if (sub_index != -1) {
				script_name = script_name_temp.substring(sub_index + 1);
			} else {
				script_name = script_name_temp;
			}

			tokenizer = new StringTokenizer(url_key_values, "&", false);
			for (; tokenizer.hasMoreTokens();) {
				String token = tokenizer.nextToken();
				if (token.indexOf('=') != -1) {
					String key = token.substring(0, token.indexOf('='));
					url_key_set.append(key);
				}
			}
		} else {
			/** �����ڲ����б� */
			int sub_index = full_url.lastIndexOf('/');
			if (sub_index != -1) {
				script_name = full_url.substring(sub_index + 1);
			} else {
				script_name = full_url;
			}
		}
		return new String[] { script_name, url_key_set.toString() };
	}

	/**
	 * ����httpЭ��ͷ�����ֵ�host��ǩ<Host: www.duyp.cn>,��ȡ��Ӧ����
	 * 
	 * @param line
	 * @return ���ؽ����õ���host��ǩ�ֶ�ȡֵ
	 */
	static public String process_host_tag(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line.trim(), ":", false);
		/** ����host��ֵ */
		tokenizer.nextToken();
		String host = tokenizer.nextToken().trim();
		return host;
	}

	/**
	 * ����httpЭ��ͷ�����ֵ�content_type��ǩ<Content-Type:
	 * application/x-www-form-urlencoded>,��ȡ��Ӧ����
	 * 
	 * @param line
	 * @return
	 */
	static public String process_content_type(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line.trim(), ":", false);
		/** ����host��ֵ */
		tokenizer.nextToken();
		String content_type = tokenizer.nextToken().trim();
		return content_type;
	}

	/**
	 * ����httpЭ��ͷ�����ֵ�cookie��ǩ,��ȡ��Ӧ����
	 * <P>
	 * Cookie: ASPSESSIONIDCCBBQSTR=DOMHJMOCAEDFHLCNLNPJFBCI; AJSTAT_ok_pages=1
	 * </P>
	 * 
	 * @param line
	 * @param ����http_post��Я����cookie��������
	 */
	static public String process_cookie_tag(String line) {
		String cookie = line.substring(line.indexOf(':') + 1).trim();
		if (cookie.length() == 0) {
			return null;
		}

		StringTokenizer tokenizer = new StringTokenizer(cookie.trim(), ";",
				false);
		StringBuilder cookie_key_set = new StringBuilder();
		for (; tokenizer.hasMoreTokens();) {
			String key_value = tokenizer.nextToken();
			int index = key_value.indexOf('=');
			if (index != -1) {
				cookie_key_set.append(key_value.substring(0, index).trim());
			}
		}
		return cookie_key_set.toString();
	}

	/**
	 * ����name1=value1&name2=value2&...&nameN=valueN��ʽ��http-body��Ϣ,��ȡ��Ӧ��ֵ
	 * 
	 * @param body
	 * @return ����http_post��Я���ı������б�
	 */
	static public String process_http_body(String body) {
		StringTokenizer tokenizer = new StringTokenizer(body, "&", false);
		StringBuilder http_body_key_set = new StringBuilder();

		for (; tokenizer.hasMoreTokens();) {
			String key_value = tokenizer.nextToken();
			int index = key_value.indexOf('=');
			if (index != -1) {
				http_body_key_set.append(key_value.substring(0, index).trim());
			}
		}
		return http_body_key_set.toString();
	}

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//		boolean whether_parse_hdr = false;
//		File[] file_set_1 = new File[] { new File("fenghuo_box_site") };
//
//		batch_fenghuo_processor p1 = new batch_fenghuo_processor(
//				"fenghuo-processor-1", file_set_1, whether_parse_hdr);
//
//		ExecutorService pool = Executors.newFixedThreadPool(2);
//		// pool.execute(p1);
//		p1.run();
		CharBuffer s = CharBuffer.allocate(1000) ;
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("c:\\post.txt")));
		//System.out.println(s.toString());
		String[] result = process_protocol_body("", r);
		for(String re : result)
			System.out.println(re);
		
	}
}