package vdx.mtg4s.terminal

import org.jline.keymap.BindingReader
import org.jline.terminal.TerminalBuilder

trait Terminal {
  def writer(): Terminal.Writer
  def reader(): Terminal.Reader
  def flush(): Unit
  def getCursorPosition(): (Terminal.Row, Terminal.Column)
}

object Terminal {
  type Row = Int
  type Column = Int
  trait Writer {
    def write(s: String): Unit
  }

  trait Reader {
    def readchar(): Int
  }

  def apply: Terminal = new Terminal {

    def writer(): Writer = _writer

    def reader(): Reader = _reader

    def flush(): Unit = underlying.flush()

    def getCursorPosition(): (Int, Int) = {
      val pos = underlying.getCursorPosition(_ => ())

      (pos.getY() + 1, pos.getX() + 1)
    }

    private val underlying = TerminalBuilder
      .builder()
      .system(true)
      .jansi(true)
      .build()

    private val _writer = new Writer {
      def write(s: String): Unit = underlying.writer().write(s)
    }

    private val _reader = new Reader {
      def readchar(): Int = bindingReader.readCharacter()
      private val bindingReader = new BindingReader(underlying.reader())
    }
  }
}
