package lniHelper;

public class Index {

	public static void parseSheet(String name, String path, Sheet sheet) throws Exception {
		Optional.ofNullable(path).filter(Utils::isNotEmpty).orElseThrow(() -> new Exception("sheet��������"));

		StringBuffer buffer = new StringBuffer();
		List<String> titles = new ArrayList<>();

		// ��ȡ�ֶ���titles
		Optional.ofNullable(sheet.getRow(1)).map(row -> {
			StreamSupport.stream(Spliterators.spliteratorUnknownSize(row.iterator(), Spliterator.ORDERED), false)
					.forEach(cell -> {
						Optional.ofNullable(cell).map(Utils::cellString).ifPresent(titles::add);
					});
			return row;
		}).orElseThrow(() -> new Exception("�յı��"));
		//titles.stream().forEach(System.out::println); //��ӡtitles
		
		// ����ÿһ��
		try {
			StreamSupport.stream(Spliterators.spliteratorUnknownSize(sheet.iterator(), Spliterator.ORDERED), false)
				.skip(2).forEach(row -> {
					Optional.of(row.getCell(1)).map(Utils::cellString).filter(Utils::isNotEmpty).ifPresent(v->{
						buffer.append(String.format("[%s]\n", v)); //ƴ��ID
					});
					
					StreamSupport.stream(Spliterators.spliteratorUnknownSize(row.iterator(), Spliterator.ORDERED),false)
						.skip(2).forEach(cell->{
							try {
								Optional.ofNullable(cell).map(Utils::getValue).filter(Utils::isNotEmpty).ifPresent(v->{
									buffer.append(String.format("%s = %s\n", titles.get(cell.getColumnIndex()), v)); //ƴ������
								});
							} catch (Exception e) {
								
							}
						});
					buffer.append("\n");
				});

		} catch (Exception e) {

		}

		// ����ļ�
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
			throw new Exception("д��ʧ�� " + file.getPath());
		}
	}
}
