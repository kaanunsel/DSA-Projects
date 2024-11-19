different_line_numbers = []

def compare_files_line_by_line(file1, file2):
    total_lines = 0
    different_lines = 0
    count = 1
    with open(file1, 'r', encoding='utf-8') as f1, open(file2, 'r', encoding='utf-8') as f2:
        while True:
            line1 = f1.readline()
            line2 = f2.readline()

            # Dosya sonuna gelindiğinde döngüden çık
            if not line1 and not line2:
                break

            total_lines += 1

            # Satırlar aynı değilse farklı satır sayısını artır
            if line1 != line2:
                different_lines += 1
                different_line_numbers.append(count)

            count += 1

    return total_lines, different_lines

if __name__ == "__main__":
    file1 = ""
    file2 = ""

    total_lines, different_lines = compare_files_line_by_line(file1, file2)

    if different_lines == 0:
        print("Dosyalar aynı.")
    else:
        print("Dosyalar farklı.")
    print(f"Toplam satır sayısı: {total_lines}")
    print(f"Farklı satır sayısı: {different_lines}")

    print(different_line_numbers)