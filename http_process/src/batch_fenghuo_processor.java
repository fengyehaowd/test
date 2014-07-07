

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
 * 用于.http及.hdr文件的转换处理
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
	 * 将信息输出到指定输出流中
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

		/** 依次处理各个目录夹 */
		for (File input : input_file_list) {
			/** 1.判断文件存储目录是否满足 */
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
				/** 依次遍历input下面所有web_site文件 */
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
					/** 2.依次遍历处理列表中数据 */

					/** 获取input目录下,所有以.http方式结尾的文件列表 */
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
	 * 给出截获的一次http_post操作名字,由两个文件组成,分别如下:
	 * <P>
	 * 2399285a-3e0d-11e2-c000-0015177a6e0d-2481159056-20434.http(存储具体http
	 * post内容)
	 * </P>
	 * <P>
	 * 2399285a-3e0d-11e2-c000-0015177a6e0d-2481159056-20434.http.hdr(
	 * 存储相应的TCP头部信息等)
	 * </P>
	 * 
	 * 目前支持三种hash计算方式:
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
	 *            存储.http及.http.hdr文件的工作目录
	 * @param http_post_name
	 *            需要解析处理的.http文件
	 * @param whether_parse_hdr
	 *            是否需要处理.hdr文件
	 * @param output
	 *            负责将<host,script,hash1,hash2,hash3>写入最终的结果文件
	 * @param console
	 *            负责将操作过程中的标准输出写入到结果文件
	 * @param error
	 *            负责将操作过程中的异常输出写入到结果文件
	 * @throws IOException
	 */
//	public void process_single_http_post(File dir, String http_post_name,
//			boolean whether_parse_hdr, BufferedWriter output,
//			BufferedWriter console, BufferedWriter error) throws IOException {
//		/** 1.打开http post对应文件,并进行相应判断 */
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
//				/** 如果需要处理.hdr文件,则调用该函数 */
//				process_header(header_reader, output, console, error);
//			}
//			/**
//			 * process_protocol_body函数返回长度为6的数组,依次为
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
//			 * </P> 目前支持三种hash计算方式:
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
	 * 处理.hdr文件
	 * 
	 * @param header_reader
	 *            读取.hdr文件使用的BufferedReader
	 * @param output
	 *            将处理结果写入到输出结果文件
	 * @param console
	 *            将日志写出到终端使用的BufferedWriter
	 * @param error
	 *            将错误信息写出到终端使用的BufferedWriter
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
	 * 从报文的三个部分提取脚本特征信息,主要包括如下几方面的信息:
	 * <P>
	 * <request line>在url中通过?name1=value1&...&nameN=valueN方式携带的变量
	 * </P>
	 * <P>
	 * <headers/cookie>采用A1=B1;...;AN=BN方式携带的变量名
	 * </P>
	 * <P>
	 * <request-body>采用name1=value1&name2=value2&...&nameN=valueN方式携带的变量
	 * </P>
	 * <P>
	 * 附件名
	 * </P>
	 * 
	 * TODO：1,查一下http标准规范中附件名出现方式; 2,查一下cookie中携带变量的方式
	 * 
	 * @param dir
	 *            存储http_post_file_name文件的工作目录
	 * @param http_post_file_name
	 *            等待处理的http_post文件名字
	 * @param reader
	 *            读取.http文件使用的Reader对象
	 * @param console
	 * @param error
	 * @return 返回长度为6的数组,依次为
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
		 * 解析http_post报文内容,报文内容包括如下四个部分,需要分别解析提取:
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
		 * </P>利用文件来记录错误行为是不错的。
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(new StringBufferInputStream(http_post)));
		BufferedWriter console = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:\\console")));
		BufferedWriter error = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:\\error")));
		boolean after_request_line = false, after_header = false, content_type_detect = false, is_supported_content_type = true;
		String line = null;
		StringBuilder http_post_body_builder = new StringBuilder();
		/** 依次读取http_post部分的数据 */
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
				/** 正在处理<headers>部分 */
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
					/** 目前只处理content_type中包含text内容的http_post信息,其它先略过 */
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
				/** blank line,跳过 */
				after_header = true;
				if (!content_type_detect) {
					log(console,
							"http_post header process finished, but no content_type detect");
				}
				continue;
			}

			/** 如果headers部门已经处理完毕,同时,http_post的content_type为支持的类型,则将相应的line添加 */
			if (after_header && line.length() > 0 && is_supported_content_type) {
				http_post_body_builder.append(line.toString()).append("&");
			}
		}

		/** 可能存在http_post_body部分为null的情况,这种情况下,http_post_body_builder的长度应该小于1 */
		if (http_post_body_builder.length() > 1) {
			http_post_body_builder
					.deleteCharAt(http_post_body_builder.length() - 1);
			/** 处理body部分 */
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
 * 解析http_post报文内容,报文内容包括如下四个部分,需要分别解析提取:
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
 * </P>利用文件来记录错误行为是不错的。
 */
//BufferedReader reader = new BufferedReader(new InputStreamReader(new StringBufferInputStream(http_post)));
BufferedWriter console = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:\\console")));
BufferedWriter error = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:\\error")));
boolean after_request_line = false, after_header = false, content_type_detect = false, is_supported_content_type = true;
String line = null;
StringBuilder http_post_body_builder = new StringBuilder();
/** 依次读取http_post部分的数据 */
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
		/** 正在处理<headers>部分 */
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
			/** 目前只处理content_type中包含text内容的http_post信息,其它先略过 */
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
		/** blank line,跳过 */
		after_header = true;
		if (!content_type_detect) {
			log(console,
					"http_post header process finished, but no content_type detect");
		}
		continue;
	}

	/** 如果headers部门已经处理完毕,同时,http_post的content_type为支持的类型,则将相应的line添加 */
	if (after_header && line.length() > 0 && is_supported_content_type) {
		http_post_body_builder.append(line.toString()).append("&");
	}
}

/** 可能存在http_post_body部分为null的情况,这种情况下,http_post_body_builder的长度应该小于1 */
if (http_post_body_builder.length() > 1) {
	http_post_body_builder
			.deleteCharAt(http_post_body_builder.length() - 1);
	/** 处理body部分 */
	body_key_set = process_http_body(http_post_body_builder.toString());
}

String[] result = { host, script, url_key_set, cookie_key_set,
		body_key_set, attachment_file_name };
return result;
}

	/**
	 * 从request_line中URL里面提取参数列表,查看是否满足?name1=value1&...&name_N=value_N
	 * 
	 * @param request_line
	 */
	static public String[] process_request_line(String request_line) {
		StringTokenizer tokenizer = new StringTokenizer(request_line, " ",
				false);
		/** 跳过action[POST|GET] */
		tokenizer.nextToken();
		String full_url = tokenizer.nextToken();
		/** 跳过HTTP/1.1 VERSION */
		tokenizer.nextToken();

		String script_name = null;
		StringBuilder url_key_set = new StringBuilder();
		/** 获取参数开始下标 */
		int index = full_url.indexOf('?');
		if (index != -1) {
			/** 存在参数列表 */
			String url_key_values = full_url.substring(index + 1);

			/** 解析host后面'/'与'?'之间内容,提取有效的script_name */
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
			/** 不存在参数列表 */
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
	 * 处理http协议头部出现的host标签<Host: www.duyp.cn>,获取相应内容
	 * 
	 * @param line
	 * @return 返回解析得到的host标签字段取值
	 */
	static public String process_host_tag(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line.trim(), ":", false);
		/** 跳过host键值 */
		tokenizer.nextToken();
		String host = tokenizer.nextToken().trim();
		return host;
	}

	/**
	 * 处理http协议头部出现的content_type标签<Content-Type:
	 * application/x-www-form-urlencoded>,获取相应内容
	 * 
	 * @param line
	 * @return
	 */
	static public String process_content_type(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line.trim(), ":", false);
		/** 跳过host键值 */
		tokenizer.nextToken();
		String content_type = tokenizer.nextToken().trim();
		return content_type;
	}

	/**
	 * 处理http协议头部出现的cookie标签,获取相应内容
	 * <P>
	 * Cookie: ASPSESSIONIDCCBBQSTR=DOMHJMOCAEDFHLCNLNPJFBCI; AJSTAT_ok_pages=1
	 * </P>
	 * 
	 * @param line
	 * @param 返回http_post中携带的cookie变量集合
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
	 * 处理name1=value1&name2=value2&...&nameN=valueN格式的http-body信息,提取相应键值
	 * 
	 * @param body
	 * @return 返回http_post中携带的变量名列表
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