package lniHelper;

public class Index {

	public static void parseSheet(String name, String path, Sheet sheet) throws Exception {
		Optional.ofNullable(path).filter(Utils::isNotEmpty).orElseThrow(() -> new Exception("sheet命名错误"));

		StringBuffer buffer = new StringBuffer();
		List<String> titles = new ArrayList<>();

		// 获取字段名titles
		Optional.ofNullable(sheet.getRow(1)).map(row -> {
			StreamSupport.stream(Spliterators.spliteratorUnknownSize(row.iterator(), Spliterator.ORDERED), false)
					.forEach(cell -> {
						Optional.ofNullable(cell).map(Utils::cellString).ifPresent(titles::add);
					});
			return row;
		}).orElseThrow(() -> new Exception("空的表格"));
		//titles.stream().forEach(System.out::println); //打印titles
		
		// 遍历每一行
		try {
			StreamSupport.stream(Spliterators.spliteratorUnknownSize(sheet.iterator(), Spliterator.ORDERED), false)
				.skip(2).forEach(row -> {
					Optional.of(row.getCell(1)).map(Utils::cellString).filter(Utils::isNotEmpty).ifPresent(v->{
						buffer.append(String.format("[%s]\n", v)); //拼接ID
					});
					
					StreamSupport.stream(Spliterators.spliteratorUnknownSize(row.iterator(), Spliterator.ORDERED),false)
						.skip(2).forEach(cell->{
							try {
								Optional.ofNullable(cell).map(Utils::getValue).filter(Utils::isNotEmpty).ifPresent(v->{
									buffer.append(String.format("%s = %s\n", titles.get(cell.getColumnIndex()), v)); //拼接属性
								});
							} catch (Exception e) {
								
							}
						});
					buffer.append("\n");
				});

		} catch (Exception e) {

		}

		// 输出文件
		output(new File(path), buffer.toString());
	}

	public static void output(File file, String str) throws Exception {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		if (file.exists())
			file.delete();

		file.createNewFile();

		try (FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);) {
			bw.write(str);
		} catch (Exception e) {
			throw new Exception("写入失败 " + file.getPath());
		}
	}
}
