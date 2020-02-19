package dev.mvc.tool;

public class PageMaker {
	private int totalcount;// ��ü �Խù� ����
	// 1 ~ 9 (1 * 9) - 8
	// 10 ~ 18 (2 * 9) - 8
	// 19 ~ 27 (3 * 9) - 8
	// 28 ~ 36 (4 * 9) - 8
	private int pagenum;// ���� ������ ��ȣ
	//private int contentnum = 3;// ���� �������� � ǥ��
	private int contentnum = 9;// ���� �������� � ǥ��
	private int startPageNum;
	private int endPageNum;

	private int startPage = 1;// ���� ������ ����� ����������
	private int endPage = 5;// ���� ������ ����� ��������

	private int currentblock;// ���� ������ ���
	private int lastblock;// ������ ������ ���
	private boolean prev = false; // ����
	private boolean next =false;// ����

	public int getStartPageNum() {
		return startPageNum;
	}

	public void setStartPageNum(int pagenum) {
		// 1 ~ 2 1 * 2
		// 3 ~ 4 
		// 5 ~ 6
		this.startPageNum = (pagenum * this.contentnum) - 8;
		//this.startPageNum = (pagenum * this.contentnum) - 1;
	}

	public int getEndPageNum() {
		return endPageNum;
	}

	public void setEndPageNum(int pagenum) {
		this.endPageNum = (this.pagenum * this.contentnum);
	}

	public void prevnext(int pagenum) {
			
		if(getCurrentblock() == 1) {
			if(5 > getEndPage()) {
				setPrev(false);
				setNext(false);
			}
			else {
				setPrev(false);
				setNext(true);
			}
			
		} else if (getLastblock() == getCurrentblock()) {
			setPrev(true);
			setNext(false);
		}else {
			setPrev(true);
			setNext(true);
		}
	}

	public int calcpage(int totalcount, int contentnum) {
		int totalpage = totalcount / contentnum;
		if (totalcount % contentnum > 0) {
			totalpage++;
		}
		return totalpage;
	}

	public int getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}

	public int getPagenum() {
		return pagenum;
	}

	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}

	public int getContentnum() {
		return contentnum;
	}

	public void setContentnum(int contentnum) {
		this.contentnum = contentnum;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int currentblock) {
		// 1 ~ 5
		// 6 ~ 10
		this.startPage = (currentblock * 5) - 4;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int getlastblock, int getcurrentblock) {
		if (getlastblock == getcurrentblock) {
			this.endPage = calcpage(getTotalcount(), getContentnum());
		} else {
			this.endPage = getStartPage() + 4;
		}

	}

	public int getCurrentblock() {
		return currentblock;
	}

	public void setCurrentblock(int pagenum) {

		// ������ ��ȣ�� ���ؼ� ���Ѵ�.
		// 1p 1 / 5 = 1
		// 6 6 / 5 = 1.2 + 1
		this.currentblock = pagenum / 5;
		if (pagenum % 5 > 0) {
			this.currentblock++;
		}
	}

	public int getLastblock() {
		return lastblock;
	}

	public void setLastblock(int totalcount) {

		this.lastblock = totalcount / (5 * this.contentnum);
		if (totalcount % (5 * this.contentnum) > 0) {
			this.lastblock++;
		}
	}

	public boolean isPrev() {
		return prev;
	}

	public void setPrev(boolean prev) {
		this.prev = prev;
	}
	

	public boolean isNext() {
		return next;
	}

	public void setNext(boolean next) {
		this.next = next;
	}

}
